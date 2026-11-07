package com.hospital.service;

import com.hospital.dto.DoctorDto;
import com.hospital.dto.TimeSlotDto;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.DoctorAvailability;
import com.hospital.entity.User;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorAvailabilityRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,
                         UserRepository userRepository,
                         DoctorAvailabilityRepository availabilityRepository,
                         AppointmentRepository appointmentRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));
        return mapToDto(doctor);
    }

    public DoctorDto getDoctorByUserId(Long userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found for user ID: " + userId));
        return mapToDto(doctor);
    }

    public List<DoctorDto> searchDoctors(String specialization, String query) {
        String cleanSpec = (specialization != null && !specialization.trim().isEmpty() && !specialization.equalsIgnoreCase("ALL"))
                ? specialization.trim() : null;
        String cleanQuery = (query != null && !query.trim().isEmpty())
                ? query.trim() : null;

        return doctorRepository.searchDoctors(cleanSpec, cleanQuery).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<String> getSpecializations() {
        return doctorRepository.findDistinctSpecializations();
    }

    @Transactional
    public DoctorDto createDoctor(DoctorDto dto) {
        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        String rawPassword = (dto.getPassword() != null && !dto.getPassword().trim().isEmpty())
                ? dto.getPassword() : "doctor123";
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("DOCTOR");
        user.setPhone(dto.getPhone());
        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setSpecialization(dto.getSpecialization().trim());
        doctor.setQualification(dto.getQualification().trim());
        doctor.setExperience(dto.getExperience() != null ? dto.getExperience() : 0);
        doctor.setConsultationFee(dto.getConsultationFee() != null ? dto.getConsultationFee() : BigDecimal.valueOf(50.00));
        doctor.setAvailabilityStatus(dto.getAvailabilityStatus() != null ? dto.getAvailabilityStatus() : "AVAILABLE");
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Seed default availability (Monday to Friday, 09:00 - 17:00)
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        for (String day : days) {
            DoctorAvailability avail = new DoctorAvailability(
                    savedDoctor,
                    day,
                    LocalTime.of(9, 0),
                    LocalTime.of(17, 0)
            );
            availabilityRepository.save(avail);
        }

        return mapToDto(savedDoctor);
    }

    @Transactional
    public DoctorDto updateDoctor(Long id, DoctorDto dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));

        User user = doctor.getUser();
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userRepository.save(user);

        if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
        if (dto.getQualification() != null) doctor.setQualification(dto.getQualification());
        if (dto.getExperience() != null) doctor.setExperience(dto.getExperience());
        if (dto.getConsultationFee() != null) doctor.setConsultationFee(dto.getConsultationFee());
        if (dto.getAvailabilityStatus() != null) doctor.setAvailabilityStatus(dto.getAvailabilityStatus());

        return mapToDto(doctorRepository.save(doctor));
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + id));
        User user = doctor.getUser();
        doctorRepository.delete(doctor);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    public List<DoctorAvailability> getDoctorAvailability(Long doctorId) {
        return availabilityRepository.findByDoctorId(doctorId);
    }

    @Transactional
    public List<DoctorAvailability> saveDoctorAvailability(Long doctorId, List<DoctorAvailability> newAvailabilities) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        availabilityRepository.deleteByDoctorId(doctorId);
        for (DoctorAvailability avail : newAvailabilities) {
            avail.setDoctor(doctor);
            avail.setId(null);
            availabilityRepository.save(avail);
        }
        return availabilityRepository.findByDoctorId(doctorId);
    }

    /**
     * Calculates available time slots for a specific doctor on a selected date.
     * Checks doctor's scheduled working hours and excludes any booked slots.
     */
    public List<TimeSlotDto> getAvailableTimeSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        if ("UNAVAILABLE".equalsIgnoreCase(doctor.getAvailabilityStatus()) ||
            "ON_LEAVE".equalsIgnoreCase(doctor.getAvailabilityStatus())) {
            return Collections.emptyList();
        }

        String dayOfWeek = date.getDayOfWeek().name();
        Optional<DoctorAvailability> availOpt = availabilityRepository.findByDoctorIdAndDayOfWeekIgnoreCase(doctorId, dayOfWeek);

        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        if (availOpt.isPresent()) {
            start = availOpt.get().getStartTime();
            end = availOpt.get().getEndTime();
        } else {
            // If Sunday, doctor is off unless explicitly configured
            if (date.getDayOfWeek().getValue() == 7) {
                return Collections.emptyList();
            }
        }

        // Get booked appointments for this doctor on this date
        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentDateAndStatusNot(doctorId, date, "CANCELLED");

        Set<LocalTime> bookedTimes = bookedAppointments.stream()
                .map(Appointment::getAppointmentTime)
                .collect(Collectors.toSet());

        List<TimeSlotDto> slots = new ArrayList<>();
        LocalTime current = start;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        while (current.isBefore(end)) {
            String timeStr = current.format(timeFormatter);
            boolean isBooked = bookedTimes.contains(current);
            boolean isPast = date.isEqual(today) && current.isBefore(now);

            if (isBooked) {
                slots.add(new TimeSlotDto(timeStr, false, "Booked"));
            } else if (isPast) {
                slots.add(new TimeSlotDto(timeStr, false, "Time Passed"));
            } else {
                slots.add(new TimeSlotDto(timeStr, true, "Available"));
            }
            current = current.plusMinutes(30); // 30-minute consultation slots
        }

        return slots;
    }

    private DoctorDto mapToDto(Doctor doctor) {
        return new DoctorDto(
                doctor.getId(),
                doctor.getUser() != null ? doctor.getUser().getId() : null,
                doctor.getUser() != null ? doctor.getUser().getName() : "Unknown",
                doctor.getUser() != null ? doctor.getUser().getEmail() : "",
                doctor.getUser() != null ? doctor.getUser().getPhone() : "",
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperience(),
                doctor.getConsultationFee(),
                doctor.getAvailabilityStatus(),
                doctor.getDepartment(),
                doctor.getConsultationRoom(),
                doctor.getAvgConsultationDuration()
        );
    }
}
