package com.hospital.service;

import com.hospital.dto.ChatRequest;
import com.hospital.dto.ChatResponse;
import com.hospital.dto.SpecialistRecommendationResponse;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ChatbotService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotService.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialistFinderService specialistFinderService;

    @Value("${hospital.ai.api-key:#{null}}")
    private String aiApiKey;

    private static final List<String> EMERGENCY_KEYWORDS = Arrays.asList(
            "chest pain", "heart attack", "cannot breathe", "can't breathe", "shortness of breath",
            "unconscious", "stroke", "bleeding heavily", "severe bleeding", "poison", "suicide", "collapse"
    );

    public ChatbotService(AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          SpecialistFinderService specialistFinderService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.specialistFinderService = specialistFinderService;
    }

    public ChatResponse processChat(ChatRequest request) {
        String msg = request.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            return new ChatResponse("Hello! I am your AI Hospital Assistant. How can I help you today? You can ask about appointments, doctors, wait times, or symptom routing.");
        }

        String lower = msg.toLowerCase().trim();

        // 1. EMERGENCY CHECK (Highest Priority)
        for (String em : EMERGENCY_KEYWORDS) {
            if (lower.contains(em)) {
                ChatResponse resp = new ChatResponse();
                resp.setEmergency(true);
                resp.setReply("EMERGENCY ALERT: Your reported symptoms indicate a potential medical emergency. Please dial emergency services (911 / 112 / 108) or go to the nearest emergency room immediately. Do not wait for a scheduled appointment.");
                resp.setActionType("EMERGENCY");
                resp.getButtons().add(new ChatResponse.ChatActionButton("Call Emergency (112 / 911)", "CALL", "tel:112"));
                resp.getButtons().add(new ChatResponse.ChatActionButton("Hospital Emergency Desk", "NAVIGATE", "#contact"));
                return resp;
            }
        }

        // 2. PRESCRIPTION / DIRECT DIAGNOSIS GUARDRAIL
        if (lower.contains("prescribe") || lower.contains("what medicine") || lower.contains("which medicine")
                || lower.contains("give me medicine") || lower.contains("antibiotic") || lower.contains("diagnose me")) {
            ChatResponse resp = new ChatResponse();
            resp.setReply("I cannot prescribe medications or make medical diagnoses. As an AI hospital assistant, I can help connect you with our qualified specialists who can review your symptoms and prescribe appropriate treatment safely.");
            resp.setActionType("MEDICAL_SAFETY");
            resp.getButtons().add(new ChatResponse.ChatActionButton("Find a Specialist", "NAVIGATE", "patient-dashboard.html#symptom-finder"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("View All Doctors", "NAVIGATE", "doctors.html"));
            return resp;
        }

        // 3. APPOINTMENT TIME / STATUS INQUIRY
        if (lower.contains("what time is my appointment") || lower.contains("when is my appointment")
                || lower.contains("my appointment") || lower.contains("appointment status") || lower.contains("show my appointments")) {
            if (request.getPatientId() != null) {
                List<Appointment> upcoming = appointmentRepository.findUpcomingAppointmentsByPatient(request.getPatientId(), LocalDate.now());
                if (!upcoming.isEmpty()) {
                    Appointment apt = upcoming.get(0);
                    String docName = apt.getDoctor() != null ? apt.getDoctor().getUser().getName() : "Doctor";
                    String room = apt.getDoctor() != null && apt.getDoctor().getConsultationRoom() != null ? apt.getDoctor().getConsultationRoom() : "Clinic 1";
                    String text = String.format("You have an appointment on %s at %s with Dr. %s (%s) in Consultation Room %s. Current status: %s.",
                            apt.getAppointmentDate(), apt.getAppointmentTime(), docName,
                            apt.getDoctor() != null ? apt.getDoctor().getSpecialization() : "",
                            room, apt.getStatus());

                    ChatResponse resp = new ChatResponse(text);
                    resp.setActionType("VIEW_APPOINTMENTS");
                    resp.getButtons().add(new ChatResponse.ChatActionButton("View Appointments", "NAVIGATE", "appointments.html"));
                    resp.getButtons().add(new ChatResponse.ChatActionButton("View QR Digital Pass", "VIEW_QR", String.valueOf(apt.getId())));
                    resp.getButtons().add(new ChatResponse.ChatActionButton("Release / Reschedule", "RELEASE_SLOT", String.valueOf(apt.getId())));
                    return resp;
                } else {
                    ChatResponse resp = new ChatResponse("You currently have no upcoming appointments scheduled. Would you like to book one with one of our specialists?");
                    resp.getButtons().add(new ChatResponse.ChatActionButton("Book Appointment", "NAVIGATE", "book-appointment.html"));
                    resp.getButtons().add(new ChatResponse.ChatActionButton("Browse Doctors", "NAVIGATE", "doctors.html"));
                    return resp;
                }
            } else {
                ChatResponse resp = new ChatResponse("Please log in to your patient account to view your appointment times and details.");
                resp.getButtons().add(new ChatResponse.ChatActionButton("Login", "NAVIGATE", "login.html"));
                return resp;
            }
        }

        // 4. CANCEL / RESCHEDULE / CANNOT ATTEND
        if (lower.contains("cannot attend") || lower.contains("can't attend") || lower.contains("cancel")
                || lower.contains("reschedule") || lower.contains("swap") || lower.contains("release")) {
            ChatResponse resp = new ChatResponse("You can cancel your appointment or release it to our Slot Marketplace so another patient can claim it. If you need a new date, you can also join the waitlist or rebook.");
            resp.setActionType("MANAGE_APPOINTMENT");
            resp.getButtons().add(new ChatResponse.ChatActionButton("My Appointments", "NAVIGATE", "appointments.html"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("Slot Marketplace", "NAVIGATE", "patient-dashboard.html#marketplace"));
            return resp;
        }

        // 5. WAITING TIME INQUIRY
        if (lower.contains("waiting time") || lower.contains("how long wait") || lower.contains("queue") || lower.contains("wait time")) {
            ChatResponse resp = new ChatResponse("Estimated waiting times are calculated dynamically based on the doctor's real-time queue and consultation duration (average 15 minutes per patient). You can view the live countdown on your Digital QR Pass.");
            resp.setActionType("WAITING_TIME");
            resp.getButtons().add(new ChatResponse.ChatActionButton("View My Appointments", "NAVIGATE", "appointments.html"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("Smart Slot Booking", "NAVIGATE", "book-appointment.html"));
            return resp;
        }

        // 6. QR APPOINTMENT CARD
        if (lower.contains("qr") || lower.contains("digital card") || lower.contains("pass") || lower.contains("check in") || lower.contains("check-in")) {
            ChatResponse resp = new ChatResponse("Every confirmed appointment includes a secure Digital QR Pass with your appointment token, consultation room, and estimated waiting time. Show this at the hospital reception for fast check-in.");
            resp.setActionType("QR_PASS");
            resp.getButtons().add(new ChatResponse.ChatActionButton("My Appointments & Passes", "NAVIGATE", "appointments.html"));
            return resp;
        }

        // 7. WAITLIST INQUIRY
        if (lower.contains("waitlist") || lower.contains("waiting list") || lower.contains("fully booked") || lower.contains("no slot")) {
            ChatResponse resp = new ChatResponse("If your preferred doctor or date is fully booked, you can join our active Waitlist. When another patient releases their slot, our system will notify you instantly so you can claim it in the Marketplace.");
            resp.setActionType("WAITLIST");
            resp.getButtons().add(new ChatResponse.ChatActionButton("Slot Marketplace", "NAVIGATE", "patient-dashboard.html#marketplace"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("Book Doctors", "NAVIGATE", "doctors.html"));
            return resp;
        }

        // 8. SYMPTOM / SPECIALIST RECOMMENDATION ("which doctor", "headache", "chest", "skin", etc.)
        if (lower.contains("doctor") && (lower.contains("which") || lower.contains("recommend") || lower.contains("see") || lower.contains("consult"))
                || lower.contains("headache") || lower.contains("dizziness") || lower.contains("cough") || lower.contains("fever")
                || lower.contains("back pain") || lower.contains("tooth") || lower.contains("skin") || lower.contains("rash")
                || lower.contains("eye") || lower.contains("vision") || lower.contains("stomach") || lower.contains("joint")) {

            try {
                SpecialistRecommendationResponse rec = specialistFinderService.findSpecialist(msg);
                if (!rec.getRecommendations().isEmpty()) {
                    SpecialistRecommendationResponse.RecommendationItem top = rec.getRecommendations().get(0);
                    String reply = String.format("Based on the information entered, you may consider consulting a specialist in %s (%s). %s\n\n*Note: This system does not diagnose medical conditions.*",
                            top.getSpecialization(), top.getDepartment(), top.getExplanation());

                    ChatResponse resp = new ChatResponse(reply);
                    resp.setActionType("RECOMMENDATION");
                    resp.getButtons().add(new ChatResponse.ChatActionButton("View " + top.getSpecialization() + " Doctors", "NAVIGATE", "doctors.html?specialization=" + top.getSpecialization()));
                    resp.getButtons().add(new ChatResponse.ChatActionButton("Symptom Finder Tool", "NAVIGATE", "patient-dashboard.html#symptom-finder"));
                    return resp;
                }
            } catch (Exception e) {
                log.warn("Specialist routing error: {}", e.getMessage());
            }
        }

        // 9. HOSPITAL TIMINGS, SERVICES, GENERAL INFO
        if (lower.contains("timing") || lower.contains("hours") || lower.contains("open") || lower.contains("hospital")) {
            ChatResponse resp = new ChatResponse("City Care Hospital OPD consultations run Monday through Saturday from 9:00 AM to 5:00 PM. Emergency and Trauma services operate 24 hours a day, 7 days a week.");
            resp.getButtons().add(new ChatResponse.ChatActionButton("Book Appointment", "NAVIGATE", "book-appointment.html"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("View Doctors", "NAVIGATE", "doctors.html"));
            return resp;
        }

        // 10. GREETINGS & DEFAULT INTELLIGENT HELPER
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("hey") || lower.contains("good morning") || lower.contains("help")) {
            ChatResponse resp = new ChatResponse("Hello! I am your AI Hospital Assistant. I can assist you with:\n• Finding the right specialist based on symptoms\n• Checking appointment times and waiting durations\n• Slot Marketplace & Waitlists\n• Digital QR Passes for check-in\n\nHow can I help you today?");
            resp.getButtons().add(new ChatResponse.ChatActionButton("Find a Specialist", "NAVIGATE", "patient-dashboard.html#symptom-finder"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("My Appointments", "NAVIGATE", "appointments.html"));
            resp.getButtons().add(new ChatResponse.ChatActionButton("Slot Marketplace", "NAVIGATE", "patient-dashboard.html#marketplace"));
            return resp;
        }

        // General Fallback
        ChatResponse fallback = new ChatResponse("I am here to guide you with hospital services, doctor consultations, appointment schedules, and waiting times. You can describe your symptoms or ask about an appointment.");
        fallback.getButtons().add(new ChatResponse.ChatActionButton("Symptom Finder", "NAVIGATE", "patient-dashboard.html#symptom-finder"));
        fallback.getButtons().add(new ChatResponse.ChatActionButton("Browse Doctors", "NAVIGATE", "doctors.html"));
        fallback.getButtons().add(new ChatResponse.ChatActionButton("My Appointments", "NAVIGATE", "appointments.html"));
        return fallback;
    }
}
