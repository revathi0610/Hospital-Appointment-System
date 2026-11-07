package com.hospital;

import com.hospital.dto.*;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class SmartFeaturesTest {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private SpecialistFinderService specialistFinderService;

    @Autowired
    private AppointmentSwapService appointmentSwapService;

    @Autowired
    private WaitingTimeService waitingTimeService;

    @Autowired
    private AppointmentReminderScheduler reminderScheduler;

    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("Feature 1: Chatbot triage, safety guardrails & emergency detection")
    void testChatbotSafetyAndTriage() {
        // Test 1: Emergency detection
        ChatRequest emergencyReq = new ChatRequest("I have acute chest pain and trouble breathing", null, null);
        ChatResponse emergencyResp = chatbotService.processChat(emergencyReq);
        assertTrue(emergencyResp.isEmergency(), "Chatbot must flag emergency");
        assertTrue(emergencyResp.getReply().contains("EMERGENCY ALERT"), "Reply must include emergency alert");

        // Test 2: Medical prescription guardrail (chatbot must NOT prescribe medications)
        ChatRequest medReq = new ChatRequest("Can you prescribe me medicine or antibiotics for infection?", null, null);
        ChatResponse medResp = chatbotService.processChat(medReq);
        assertFalse(medResp.isEmergency());
        assertTrue(medResp.getReply().toLowerCase().contains("cannot prescribe"), "Chatbot must refuse prescribing medicine");

        // Test 3: Safe symptom routing
        ChatRequest triageReq = new ChatRequest("I have frequent headaches and dizzy spells", null, null);
        ChatResponse triageResp = chatbotService.processChat(triageReq);
        assertTrue(triageResp.getReply().contains("Neurologist") || triageResp.getReply().contains("Neurology"),
                "Chatbot should suggest Neurologist for headaches");
    }

    @Test
    @DisplayName("Feature 2: Symptom -> Specialist Finder keyword matching & emergency detection")
    void testSpecialistFinder() {
        // Non-emergency keyword search
        SpecialistRecommendationResponse rec = specialistFinderService.findSpecialist("I am experiencing chest discomfort and heart palpitations");
        assertNotNull(rec);
        assertNotNull(rec.getDisclaimer(), "Must include medical disclaimer");
        assertFalse(rec.getRecommendations().isEmpty(), "Should have matched recommendations");

        // Emergency detection
        SpecialistRecommendationResponse emRec = specialistFinderService.findSpecialist("severe chest pain heart attack can't breathe");
        assertTrue(emRec.isEmergency(), "Should identify emergency triggers");
        assertNotNull(emRec.getEmergencyAlert());
    }

    @Test
    @DisplayName("Feature 3: Appointment Release to Marketplace & Safe Claiming")
    @Transactional
    void testAppointmentReleaseAndClaim() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Patient> patients = patientRepository.findAll();
        assertFalse(doctors.isEmpty());
        assertTrue(patients.size() >= 2);

        Doctor doctor = doctors.get(0);
        Patient patient1 = patients.get(0);
        Patient patient2 = patients.get(1);

        // Create an appointment for patient 1
        Appointment apt = new Appointment();
        apt.setDoctor(doctor);
        apt.setPatient(patient1);
        apt.setAppointmentDate(LocalDate.now().plusDays(5));
        apt.setAppointmentTime(LocalTime.of(11, 0));
        apt.setStatus("CONFIRMED");
        apt.setReason("Routine Checkup");
        Appointment saved = appointmentRepository.save(apt);

        // Patient 1 releases the appointment
        Appointment released = appointmentSwapService.releaseAppointment(saved.getId(), patient1.getId());
        assertEquals("RELEASED", released.getStatus());

        // Verify it appears in Marketplace
        List<ReleasedSlotDto> marketplace = appointmentSwapService.getMarketplaceSlots();
        boolean inMarketplace = marketplace.stream().anyMatch(m -> m.getAppointmentId().equals(saved.getId()));
        assertTrue(inMarketplace, "Released slot should appear in marketplace");

        // Patient 2 claims the released slot
        Appointment claimed = appointmentSwapService.claimReleasedSlot(saved.getId(), patient2.getId());
        assertEquals("CONFIRMED", claimed.getStatus());
        assertEquals(patient2.getId(), claimed.getPatient().getId());

        // Verify attempting to claim again fails
        assertThrows(IllegalStateException.class, () -> {
            appointmentSwapService.claimReleasedSlot(saved.getId(), patient1.getId());
        });
    }

    @Test
    @DisplayName("Feature 4: Smart Slot recommendations & live waiting-time estimation")
    void testWaitingTimeAndSmartSlots() {
        List<Doctor> doctors = doctorRepository.findAll();
        assertFalse(doctors.isEmpty());
        Doctor doctor = doctors.get(0);

        List<SmartSlotDto> smartSlots = waitingTimeService.getDoctorSmartSlots(doctor.getId(), LocalDate.now().plusDays(2));
        assertNotNull(smartSlots);
        assertFalse(smartSlots.isEmpty(), "Doctor should have available smart slots");

        // Test waiting time calculation
        List<Appointment> all = appointmentRepository.findAll();
        if (!all.isEmpty()) {
            Appointment sample = all.get(0);
            WaitingTimeDto waitDto = waitingTimeService.calculateAppointmentWaitingTime(sample.getId());
            assertNotNull(waitDto);
            assertNotNull(waitDto.getEstimatedWaitFormatted());
        }
    }

    @Test
    @DisplayName("Feature 5: Automatic Appointment Reminders & Notifications")
    void testRemindersAndNotifications() {
        int count = reminderScheduler.triggerReminderScan();
        assertTrue(count >= 0, "Scan should complete without error");

        List<Patient> patients = patientRepository.findAll();
        if (!patients.isEmpty() && patients.get(0).getUser() != null) {
            Long userId = patients.get(0).getUser().getId();
            long unread = notificationService.getUnreadCount(userId);
            assertTrue(unread >= 0);
        }
    }

    @Test
    @DisplayName("Feature 6: QR Digital Appointment Card & UUID Verification")
    @Transactional
    void testDigitalCardAndQrVerification() {
        List<Appointment> all = appointmentRepository.findAll();
        assertFalse(all.isEmpty());
        Appointment apt = all.get(0);

        DigitalCardDto card = qrCodeService.getDigitalAppointmentCard(apt.getId());
        assertNotNull(card);
        assertNotNull(card.getQrToken(), "QR token UUID must be generated");
        assertNotNull(card.getQrDataString(), "QR payload string must be present");

        // Verify pass using the generated token
        QrVerifyResponse verifyResp = qrCodeService.verifyAppointmentToken(card.getQrToken());
        assertTrue(verifyResp.isValid(), "QR token should verify successfully");
        assertEquals(apt.getId(), verifyResp.getAppointmentId());

        // Test invalid token
        QrVerifyResponse invalidResp = qrCodeService.verifyAppointmentToken("invalid-fake-uuid");
        assertFalse(invalidResp.isValid(), "Fake token must not verify");
    }

    @Test
    @DisplayName("Feature 7: Doctor & Admin Operational Analytics")
    void testAnalytics() {
        List<Doctor> doctors = doctorRepository.findAll();
        assertFalse(doctors.isEmpty());
        Doctor doctor = doctors.get(0);

        DoctorAnalyticsDto docAnalytics = analyticsService.getDoctorAnalytics(doctor.getId());
        assertNotNull(docAnalytics);
        assertNotNull(docAnalytics.getStatusBreakdown());
        assertNotNull(docAnalytics.getDayOfWeekDistribution());

        AdminAnalyticsDto adminAnalytics = analyticsService.getAdminHospitalAnalytics();
        assertNotNull(adminAnalytics);
        assertTrue(adminAnalytics.getTotalDoctors() > 0);
        assertNotNull(adminAnalytics.getAppointmentsByDepartment());
        assertNotNull(adminAnalytics.getDailyVolumeRecentDays());
    }
}
