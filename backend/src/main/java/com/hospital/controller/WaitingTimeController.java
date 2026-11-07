package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.SmartSlotDto;
import com.hospital.dto.WaitingTimeDto;
import com.hospital.entity.Doctor;
import com.hospital.service.WaitingTimeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WaitingTimeController {

    private final WaitingTimeService waitingTimeService;

    public WaitingTimeController(WaitingTimeService waitingTimeService) {
        this.waitingTimeService = waitingTimeService;
    }

    // Get live estimated waiting time for an appointment
    @GetMapping("/appointments/{id}/waiting-time")
    public ResponseEntity<ApiResponse<WaitingTimeDto>> getAppointmentWaitingTime(@PathVariable Long id) {
        WaitingTimeDto dto = waitingTimeService.calculateAppointmentWaitingTime(id);
        return ResponseEntity.ok(ApiResponse.success("Waiting time calculated", dto));
    }

    // Get smart slot suggestions with wait time predictions for a doctor and date
    @GetMapping("/doctors/{id}/smart-slots")
    public ResponseEntity<ApiResponse<List<SmartSlotDto>>> getDoctorSmartSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<SmartSlotDto> slots = waitingTimeService.getDoctorSmartSlots(id, date);
        return ResponseEntity.ok(ApiResponse.success("Smart slots retrieved", slots));
    }

    // Doctor updates average consultation duration
    @PutMapping("/doctors/{id}/consultation-duration")
    public ResponseEntity<ApiResponse<Doctor>> updateConsultationDuration(
            @PathVariable Long id,
            @RequestParam int duration) {
        Doctor updated = waitingTimeService.updateDoctorAvgDuration(id, duration);
        return ResponseEntity.ok(ApiResponse.success("Consultation duration updated", updated));
    }
}
