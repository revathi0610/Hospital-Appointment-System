package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.PatientDto;
import com.hospital.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientById(@PathVariable Long id) {
        try {
            PatientDto patient = patientService.getPatientById(id);
            return ResponseEntity.ok(ApiResponse.success("Patient found", patient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientByUserId(@PathVariable Long userId) {
        try {
            PatientDto patient = patientService.getPatientByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Patient found", patient));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDto>> updateProfile(@PathVariable Long id, @RequestBody PatientDto dto) {
        try {
            PatientDto updated = patientService.updatePatientProfile(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
