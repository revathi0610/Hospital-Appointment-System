package com.hospital.controller;

import com.hospital.dto.AdminAnalyticsDto;
import com.hospital.dto.ApiResponse;
import com.hospital.dto.DoctorAnalyticsDto;
import com.hospital.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/doctors/{id}/analytics")
    public ResponseEntity<ApiResponse<DoctorAnalyticsDto>> getDoctorAnalytics(@PathVariable Long id) {
        DoctorAnalyticsDto dto = analyticsService.getDoctorAnalytics(id);
        return ResponseEntity.ok(ApiResponse.success("Doctor analytics retrieved", dto));
    }

    @GetMapping("/admin/analytics")
    public ResponseEntity<ApiResponse<AdminAnalyticsDto>> getAdminAnalytics() {
        AdminAnalyticsDto dto = analyticsService.getAdminHospitalAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Hospital admin analytics retrieved", dto));
    }
}
