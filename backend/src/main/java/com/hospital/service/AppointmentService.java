package com.hospital.service;

import com.hospital.dto.AppointmentRequest;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AppointmentService {

    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "CONFIRMED", "COMPLETED", "CANCELLED");

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public Appointment bookAppointment(AppointmentRequest request) {
        // Validate date
        LocalDate today = LocalDate.now();
        if (request.getAppointmentDate().isBefore(today)) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        if (request.getAppointmentDate().isEqual(today) && request.getAppointmentTime().isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Appointment time has already passed for today");
        }

        // Validate Patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + request.getPatientId()));

        // Validate Doctor
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + request.getDoctorId()));

        if ("UNAVAILABLE".equalsIgnoreCase(doctor.getAvailabilityStatus()) ||
            "ON_LEAVE".equalsIgnoreCase(doctor.getAvailabilityStatus())) {
            throw new IllegalStateException("Doctor is currently not available for bookings");
        }

        // Check for double booking: Doctor cannot have two appointments at the same date & time
        boolean doctorBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                doctor.getId(), request.getAppointmentDate(), request.getAppointmentTime(), "CANCELLED");
        if (doctorBooked) {
            throw new IllegalStateException("The selected doctor is already booked for this time slot. Please choose another time.");
        }

        // Check for double booking: Patient cannot book two appointments at the same date & time
        boolean patientBooked = appointmentRepository.existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                patient.getId(), request.getAppointmentDate(), request.getAppointmentTime(), "CANCELLED");
        if (patientBooked) {
            throw new IllegalStateException("You already have an active appointment booked for this time slot.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason() != null ? request.getReason().trim() : "General Consultation");
        appointment.setStatus("PENDING");

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    public List<Appointment> getUpcomingAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findUpcomingAppointmentsByPatient(patientId, LocalDate.now());
    }

    public List<Appointment> getPastAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findPastAppointmentsByPatient(patientId, LocalDate.now());
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(doctorId);
    }

    public List<Appointment> getTodayAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(doctorId, LocalDate.now());
    }

    public List<Appointment> getUpcomingAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findUpcomingAppointmentsByDoctor(doctorId, LocalDate.now());
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long id, String status) {
        String upperStatus = status.trim().toUpperCase();
        if (!VALID_STATUSES.contains(upperStatus)) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Allowed: PENDING, CONFIRMED, COMPLETED, CANCELLED");
        }

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));

        appointment.setStatus(upperStatus);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment cancelAppointment(Long id) {
        return updateAppointmentStatus(id, "CANCELLED");
    }
}
