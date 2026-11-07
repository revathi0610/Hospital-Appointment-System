package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.DoctorDashboardStatsDto;
import com.hospital.dto.DoctorDto;
import com.hospital.dto.TimeSlotDto;
import com.hospital.entity.DoctorAvailability;
import com.hospital.service.AdminService;
import com.hospital.service.DoctorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AdminService adminService;

    public DoctorController(DoctorService doctorService, AdminService adminService) {
        this.doctorService = doctorService;
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String search) {
        List<DoctorDto> doctors;
        if (specialization != null || search != null) {
            doctors = doctorService.searchDoctors(specialization, search);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        return ResponseEntity.ok(ApiResponse.success("Doctors retrieved successfully", doctors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorById(@PathVariable Long id) {
        try {
            DoctorDto doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(ApiResponse.success("Doctor details retrieved", doctor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<DoctorDto>> getDoctorByUserId(@PathVariable Long userId) {
        try {
            DoctorDto doctor = doctorService.getDoctorByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Doctor details retrieved", doctor));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<String>>> getSpecializations() {
        List<String> list = doctorService.getSpecializations();
        return ResponseEntity.ok(ApiResponse.success("Specializations retrieved", list));
    }

    @GetMapping("/{id}/available-slots")
    public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getAvailableSlots(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotDto> slots = doctorService.getAvailableTimeSlots(id, date);
            return ResponseEntity.ok(ApiResponse.success("Available slots retrieved", slots));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<List<DoctorAvailability>>> getAvailability(@PathVariable Long id) {
        List<DoctorAvailability> avail = doctorService.getDoctorAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor availability schedule retrieved", avail));
    }

    @PostMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<List<DoctorAvailability>>> updateAvailability(
            @PathVariable Long id,
            @RequestBody List<DoctorAvailability> availabilities) {
        try {
            List<DoctorAvailability> updated = doctorService.saveDoctorAvailability(id, availabilities);
            return ResponseEntity.ok(ApiResponse.success("Schedule updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/dashboard-stats")
    public ResponseEntity<ApiResponse<DoctorDashboardStatsDto>> getDashboardStats(@PathVariable Long id) {
        DoctorDashboardStatsDto stats = adminService.getDoctorDashboardStats(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor dashboard stats retrieved", stats));
    }
}
