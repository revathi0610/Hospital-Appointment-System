package com.hospital.service;

import com.hospital.dto.DigitalCardDto;
import com.hospital.dto.QrVerifyResponse;
import com.hospital.dto.WaitingTimeDto;
import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrCodeService {

    private final AppointmentRepository appointmentRepository;
    private final WaitingTimeService waitingTimeService;

    public QrCodeService(AppointmentRepository appointmentRepository,
                         WaitingTimeService waitingTimeService) {
        this.appointmentRepository = appointmentRepository;
        this.waitingTimeService = waitingTimeService;
    }

    @Transactional
    public DigitalCardDto getDigitalAppointmentCard(Long appointmentId) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + appointmentId));

        if (apt.getQrToken() == null || apt.getQrToken().trim().isEmpty()) {
            apt.setQrToken(UUID.randomUUID().toString());
            apt = appointmentRepository.save(apt);
        }

        WaitingTimeDto waitInfo = waitingTimeService.calculateAppointmentWaitingTime(appointmentId);

        String patientName = apt.getPatient() != null && apt.getPatient().getUser() != null ? apt.getPatient().getUser().getName() : "Patient";
        String patientEmail = apt.getPatient() != null && apt.getPatient().getUser() != null ? apt.getPatient().getUser().getEmail() : "";
        String patientPhone = apt.getPatient() != null && apt.getPatient().getUser() != null ? apt.getPatient().getUser().getPhone() : "";

        String doctorName = apt.getDoctor() != null && apt.getDoctor().getUser() != null ? apt.getDoctor().getUser().getName() : "Doctor";
        String spec = apt.getDoctor() != null ? apt.getDoctor().getSpecialization() : "General";
        String dept = apt.getDoctor() != null && apt.getDoctor().getDepartment() != null ? apt.getDoctor().getDepartment() : spec;
        String room = apt.getDoctor() != null && apt.getDoctor().getConsultationRoom() != null ? apt.getDoctor().getConsultationRoom() : "Consultation Room 1";

        // JSON or plain verifiable payload for QR reader
        String qrPayload = String.format("{\"token\":\"%s\",\"id\":%d,\"date\":\"%s\",\"time\":\"%s\",\"doctor\":\"%s\"}",
                apt.getQrToken(), apt.getId(), apt.getAppointmentDate(), apt.getAppointmentTime(), doctorName);

        return new DigitalCardDto(
                apt.getId(),
                patientName,
                patientEmail,
                patientPhone,
                doctorName,
                spec,
                dept,
                room,
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getStatus(),
                apt.getQrToken(),
                qrPayload,
                waitInfo.getEstimatedWaitMinutes()
        );
    }

    public QrVerifyResponse verifyAppointmentToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return QrVerifyResponse.invalid("QR token is missing or empty.");
        }

        Optional<Appointment> aptOpt = appointmentRepository.findByQrToken(token.trim());
        if (aptOpt.isEmpty()) {
            return QrVerifyResponse.invalid("Invalid QR Pass: No matching hospital appointment found for this token.");
        }

        Appointment apt = aptOpt.get();
        String patientName = apt.getPatient() != null && apt.getPatient().getUser() != null ? apt.getPatient().getUser().getName() : "Unknown";
        String doctorName = apt.getDoctor() != null && apt.getDoctor().getUser() != null ? apt.getDoctor().getUser().getName() : "Unknown";
        String spec = apt.getDoctor() != null ? apt.getDoctor().getSpecialization() : "";

        String message;
        if ("CANCELLED".equalsIgnoreCase(apt.getStatus())) {
            message = "Notice: This appointment was CANCELLED.";
        } else if (apt.getAppointmentDate().isBefore(LocalDate.now())) {
            message = "Pass Expired: Appointment was on " + apt.getAppointmentDate();
        } else {
            message = "Verified: Valid hospital appointment pass for check-in.";
        }

        return QrVerifyResponse.valid(
                apt.getId(),
                patientName,
                doctorName,
                spec,
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getStatus(),
                message
        );
    }
}
