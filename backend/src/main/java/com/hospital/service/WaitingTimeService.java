package com.hospital.service;

import com.hospital.dto.SmartSlotDto;
import com.hospital.dto.WaitingTimeDto;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.DoctorAvailability;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorAvailabilityRepository;
import com.hospital.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaitingTimeService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public WaitingTimeService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              DoctorAvailabilityRepository availabilityRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public WaitingTimeDto calculateAppointmentWaitingTime(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        WaitingTimeDto dto = new WaitingTimeDto();
        dto.setAppointmentId(appointmentId);
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setScheduledTime(appointment.getAppointmentTime());

        Doctor doctor = appointment.getDoctor();
        if (doctor != null) {
            dto.setDoctorName(doctor.getUser() != null ? doctor.getUser().getName() : "Doctor");
            dto.setDoctorSpecialization(doctor.getSpecialization());
            dto.setConsultationRoom(doctor.getConsultationRoom() != null ? doctor.getConsultationRoom() : "Cabin 1");
            int avgDuration = doctor.getAvgConsultationDuration() > 0 ? doctor.getAvgConsultationDuration() : 15;
            dto.setAvgConsultationDuration(avgDuration);

            if ("COMPLETED".equalsIgnoreCase(appointment.getStatus()) || "CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
                dto.setPatientsAhead(0);
                dto.setEstimatedWaitMinutes(0);
                dto.setExpectedConsultationTime(appointment.getAppointmentTime());
                dto.setStatusMessage("Appointment " + appointment.getStatus().toLowerCase() + ".");
                return dto;
            }

            // Find all active appointments for this doctor on the same date
            List<Appointment> dayQueue = appointmentRepository.findActiveQueue(
                    doctor.getId(),
                    appointment.getAppointmentDate(),
                    Arrays.asList("PENDING", "CONFIRMED")
            );

            // Count patients whose appointment time is strictly before this appointment
            int patientsAhead = 0;
            for (Appointment a : dayQueue) {
                if (a.getAppointmentTime().isBefore(appointment.getAppointmentTime()) && !a.getId().equals(appointmentId)) {
                    patientsAhead++;
                }
            }

            int estimatedWait = patientsAhead * avgDuration;
            dto.setPatientsAhead(patientsAhead);
            dto.setEstimatedWaitMinutes(estimatedWait);
            dto.setExpectedConsultationTime(appointment.getAppointmentTime().plusMinutes(estimatedWait));

            if (patientsAhead == 0) {
                dto.setStatusMessage("You are first in queue for your time window. Expected immediately.");
            } else {
                dto.setStatusMessage(patientsAhead + " patient(s) scheduled ahead. Estimated waiting time is approximately " + estimatedWait + " minutes.");
            }
        }

        return dto;
    }

    public List<SmartSlotDto> getDoctorSmartSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        int avgDuration = doctor.getAvgConsultationDuration() > 0 ? doctor.getAvgConsultationDuration() : 15;
        String dayOfWeek = date.getDayOfWeek().name();
        Optional<DoctorAvailability> availOpt = availabilityRepository.findByDoctorIdAndDayOfWeekIgnoreCase(doctorId, dayOfWeek);

        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);

        if (availOpt.isPresent()) {
            start = availOpt.get().getStartTime();
            end = availOpt.get().getEndTime();
        } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return Collections.emptyList();
        }

        // Fetch active appointments (not cancelled)
        List<Appointment> existingList = appointmentRepository.findByDoctorIdAndAppointmentDateAndStatusNot(
                doctorId, date, "CANCELLED"
        );

        Map<LocalTime, Appointment> slotToAppointment = new HashMap<>();
        for (Appointment a : existingList) {
            slotToAppointment.put(a.getAppointmentTime(), a);
        }

        List<SmartSlotDto> smartSlots = new ArrayList<>();
        LocalTime current = start;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        int cumulativeBookedBefore = 0;

        while (current.isBefore(end)) {
            String timeStr = current.format(timeFormatter);
            Appointment apt = slotToAppointment.get(current);

            boolean isPast = date.isEqual(today) && current.isBefore(now);
            boolean isBooked = apt != null && !"RELEASED".equalsIgnoreCase(apt.getStatus());
            boolean isReleased = apt != null && "RELEASED".equalsIgnoreCase(apt.getStatus());
            boolean isAvailable = !isBooked && !isPast;

            int waitEst = cumulativeBookedBefore * avgDuration;

            String recommendation;
            String badgeClass;

            if (isPast) {
                recommendation = "Time Passed";
                badgeClass = "secondary";
            } else if (isReleased) {
                recommendation = "Released by Patient - Instant Booking Available!";
                badgeClass = "warning";
            } else if (isBooked) {
                recommendation = "Booked";
                badgeClass = "danger";
                cumulativeBookedBefore++;
            } else {
                if (cumulativeBookedBefore == 0) {
                    recommendation = "Recommended - Earliest Slot (Near Zero Wait)";
                    badgeClass = "success";
                } else if (cumulativeBookedBefore <= 2) {
                    recommendation = "Low Wait (~" + waitEst + " mins expected)";
                    badgeClass = "info";
                } else {
                    recommendation = "Moderate Wait (~" + waitEst + " mins expected)";
                    badgeClass = "light";
                }
            }

            smartSlots.add(new SmartSlotDto(
                    timeStr,
                    isAvailable,
                    recommendation,
                    waitEst,
                    badgeClass,
                    isReleased,
                    apt != null && isReleased ? apt.getId() : null
            ));

            current = current.plusMinutes(30);
        }

        return smartSlots;
    }

    @Transactional
    public Doctor updateDoctorAvgDuration(Long doctorId, int durationMinutes) {
        if (durationMinutes < 5 || durationMinutes > 120) {
            throw new IllegalArgumentException("Duration must be between 5 and 120 minutes.");
        }
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));
        doctor.setAvgConsultationDuration(durationMinutes);
        return doctorRepository.save(doctor);
    }
}
