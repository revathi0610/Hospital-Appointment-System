package com.hospital.service;

import com.hospital.dto.ReleasedSlotDto;
import com.hospital.dto.WaitlistDto;
import com.hospital.dto.WaitlistRequest;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.entity.Waitlist;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.WaitlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentSwapService {

    private final AppointmentRepository appointmentRepository;
    private final WaitlistRepository waitlistRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NotificationService notificationService;

    public AppointmentSwapService(AppointmentRepository appointmentRepository,
                                  WaitlistRepository waitlistRepository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.waitlistRepository = waitlistRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Appointment releaseAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (appointment.getPatient() != null && !appointment.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("You are not authorized to release this appointment.");
        }

        if (!"PENDING".equalsIgnoreCase(appointment.getStatus()) && !"CONFIRMED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException("Only PENDING or CONFIRMED appointments can be released.");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot release past appointments.");
        }

        appointment.setStatus("RELEASED");
        Appointment updated = appointmentRepository.save(appointment);

        // Notify patient
        if (appointment.getPatient() != null && appointment.getPatient().getUser() != null) {
            notificationService.createNotification(
                    appointment.getPatient().getUser(),
                    "Appointment Released to Marketplace",
                    String.format("Your appointment on %s at %s with Dr. %s has been released for other patients.",
                            appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                            appointment.getDoctor().getUser().getName()),
                    "SLOT_RELEASED",
                    appointment.getId()
            );
        }

        // Notify active waitlist patients
        Doctor doc = appointment.getDoctor();
        if (doc != null) {
            List<Waitlist> waitlist = waitlistRepository.findByDoctorIdAndPreferredDateAndStatusOrderByCreatedAtAsc(
                    doc.getId(), appointment.getAppointmentDate(), "ACTIVE"
            );

            for (Waitlist w : waitlist) {
                if (w.getPatient() != null && w.getPatient().getUser() != null) {
                    notificationService.createNotification(
                            w.getPatient().getUser(),
                            "Slot Available on Waitlist!",
                            String.format("A slot on %s at %s with Dr. %s just opened in the Marketplace! Claim it now before it's gone.",
                                    appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                                    doc.getUser().getName()),
                            "WAITLIST_ALERT",
                            appointment.getId()
                    );
                }
            }
        }

        return updated;
    }

    public List<ReleasedSlotDto> getMarketplaceSlots() {
        List<Appointment> released = appointmentRepository.findByStatusOrderByAppointmentDateAscAppointmentTimeAsc("RELEASED");
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return released.stream()
                .filter(a -> a.getAppointmentDate().isAfter(today) ||
                        (a.getAppointmentDate().isEqual(today) && a.getAppointmentTime().isAfter(now)))
                .map(this::mapToReleasedDto)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Appointment claimReleasedSlot(Long appointmentId, Long claimingPatientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        // Concurrency / Race-condition guard
        if (!"RELEASED".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException("Sorry, this slot is no longer available. It may have already been claimed by another patient.");
        }

        Patient claimingPatient = patientRepository.findById(claimingPatientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + claimingPatientId));

        // Check if patient already has an appointment at this time
        boolean doubleBooked = appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                claimingPatientId, appointment.getAppointmentDate(), appointment.getAppointmentTime(), "CANCELLED"
        );
        if (doubleBooked) {
            throw new IllegalStateException("You already have an active appointment booked for this date and time.");
        }

        // Reassign and confirm
        appointment.setPatient(claimingPatient);
        appointment.setStatus("CONFIRMED");
        appointment.setReason("Claimed from Slot Marketplace");
        if (appointment.getQrToken() == null || appointment.getQrToken().isEmpty()) {
            appointment.setQrToken(UUID.randomUUID().toString());
        }

        Appointment saved = appointmentRepository.save(appointment);

        // If claiming patient had an active waitlist entry for this doc & date, mark fulfilled
        if (appointment.getDoctor() != null) {
            List<Waitlist> userWaitlists = waitlistRepository.findByPatientIdOrderByCreatedAtDesc(claimingPatientId);
            for (Waitlist w : userWaitlists) {
                if ("ACTIVE".equalsIgnoreCase(w.getStatus()) &&
                        w.getDoctor().getId().equals(appointment.getDoctor().getId()) &&
                        w.getPreferredDate().isEqual(appointment.getAppointmentDate())) {
                    w.setStatus("FULFILLED");
                    waitlistRepository.save(w);
                }
            }
        }

        // Notify claiming patient
        if (claimingPatient.getUser() != null) {
            notificationService.createNotification(
                    claimingPatient.getUser(),
                    "Slot Successfully Claimed!",
                    String.format("You secured an appointment on %s at %s with Dr. %s. Your QR Pass is ready.",
                            saved.getAppointmentDate(), saved.getAppointmentTime(),
                            saved.getDoctor().getUser().getName()),
                    "APPOINTMENT_CLAIMED",
                    saved.getId()
            );
        }

        // Notify doctor
        if (saved.getDoctor() != null && saved.getDoctor().getUser() != null) {
            notificationService.createNotification(
                    saved.getDoctor().getUser(),
                    "Slot Claimed via Marketplace",
                    String.format("Patient %s has claimed the open slot on %s at %s.",
                            claimingPatient.getUser().getName(), saved.getAppointmentDate(), saved.getAppointmentTime()),
                    "SLOT_CLAIMED",
                    saved.getId()
            );
        }

        return saved;
    }

    @Transactional
    public WaitlistDto joinWaitlist(WaitlistRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + request.getDoctorId()));

        if (request.getPreferredDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Preferred date cannot be in the past.");
        }

        boolean exists = waitlistRepository.existsByPatientIdAndDoctorIdAndPreferredDateAndStatus(
                patient.getId(), doctor.getId(), request.getPreferredDate(), "ACTIVE"
        );
        if (exists) {
            throw new IllegalStateException("You are already on the active waitlist for this doctor and date.");
        }

        Waitlist waitlist = new Waitlist(patient, doctor, request.getPreferredDate(), request.getNotes());
        Waitlist saved = waitlistRepository.save(waitlist);

        // Notify patient
        if (patient.getUser() != null) {
            notificationService.createNotification(
                    patient.getUser(),
                    "Joined Waitlist",
                    String.format("You are on the waitlist for Dr. %s on %s. We will alert you immediately if a slot opens.",
                            doctor.getUser().getName(), request.getPreferredDate()),
                    "WAITLIST_JOINED",
                    null
            );
        }

        return mapToWaitlistDto(saved);
    }

    public List<WaitlistDto> getPatientWaitlists(Long patientId) {
        return waitlistRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::mapToWaitlistDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelWaitlist(Long waitlistId, Long patientId) {
        Waitlist w = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new IllegalArgumentException("Waitlist entry not found: " + waitlistId));
        if (!w.getPatient().getId().equals(patientId)) {
            throw new IllegalArgumentException("Unauthorized to cancel this waitlist entry.");
        }
        w.setStatus("CANCELLED");
        waitlistRepository.save(w);
    }

    private ReleasedSlotDto mapToReleasedDto(Appointment a) {
        ReleasedSlotDto dto = new ReleasedSlotDto();
        dto.setAppointmentId(a.getId());
        dto.setDoctorId(a.getDoctor().getId());
        dto.setDoctorName(a.getDoctor().getUser().getName());
        dto.setSpecialization(a.getDoctor().getSpecialization());
        dto.setConsultationRoom(a.getDoctor().getConsultationRoom() != null ? a.getDoctor().getConsultationRoom() : "Cabin 1");
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setConsultationFee(a.getDoctor().getConsultationFee());
        dto.setOriginalPatientName(a.getPatient() != null && a.getPatient().getUser() != null ? a.getPatient().getUser().getName() : "Anonymous");
        return dto;
    }

    private WaitlistDto mapToWaitlistDto(Waitlist w) {
        WaitlistDto dto = new WaitlistDto();
        dto.setId(w.getId());
        dto.setPatientId(w.getPatient().getId());
        dto.setPatientName(w.getPatient().getUser().getName());
        dto.setDoctorId(w.getDoctor().getId());
        dto.setDoctorName(w.getDoctor().getUser().getName());
        dto.setSpecialization(w.getDoctor().getSpecialization());
        dto.setPreferredDate(w.getPreferredDate());
        dto.setStatus(w.getStatus());
        dto.setNotes(w.getNotes());
        dto.setCreatedAt(w.getCreatedAt());
        return dto;
    }
}
