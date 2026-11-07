package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.ReleasedSlotDto;
import com.hospital.dto.WaitlistDto;
import com.hospital.dto.WaitlistRequest;
import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentSwapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentSwapController {

    private final AppointmentSwapService swapService;

    public AppointmentSwapController(AppointmentSwapService swapService) {
        this.swapService = swapService;
    }

    // Release an appointment to the marketplace
    @PutMapping("/appointments/{id}/release")
    public ResponseEntity<ApiResponse<Appointment>> releaseAppointment(
            @PathVariable Long id,
            @RequestParam Long patientId) {
        Appointment apt = swapService.releaseAppointment(id, patientId);
        return ResponseEntity.ok(ApiResponse.success("Appointment released to marketplace successfully", apt));
    }

    // Browse marketplace for released slots
    @GetMapping("/appointments/marketplace")
    public ResponseEntity<ApiResponse<List<ReleasedSlotDto>>> getMarketplaceSlots() {
        List<ReleasedSlotDto> slots = swapService.getMarketplaceSlots();
        return ResponseEntity.ok(ApiResponse.success("Marketplace slots retrieved", slots));
    }

    // Claim a released slot
    @PostMapping("/appointments/{id}/claim")
    public ResponseEntity<ApiResponse<Appointment>> claimReleasedSlot(
            @PathVariable Long id,
            @RequestParam Long patientId) {
        Appointment apt = swapService.claimReleasedSlot(id, patientId);
        return ResponseEntity.ok(ApiResponse.success("Slot claimed successfully", apt));
    }

    // Join waitlist
    @PostMapping("/waitlist")
    public ResponseEntity<ApiResponse<WaitlistDto>> joinWaitlist(@RequestBody WaitlistRequest request) {
        WaitlistDto dto = swapService.joinWaitlist(request);
        return ResponseEntity.ok(ApiResponse.success("Added to waitlist successfully", dto));
    }

    // View patient's waitlists
    @GetMapping("/waitlist/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<WaitlistDto>>> getPatientWaitlists(@PathVariable Long patientId) {
        List<WaitlistDto> list = swapService.getPatientWaitlists(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient waitlists retrieved", list));
    }

    // Cancel waitlist entry
    @DeleteMapping("/waitlist/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelWaitlist(
            @PathVariable Long id,
            @RequestParam Long patientId) {
        swapService.cancelWaitlist(id, patientId);
        return ResponseEntity.ok(ApiResponse.success("Waitlist entry cancelled", null));
    }
}
