package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.DashboardStatsDto;
import com.hospital.dto.DoctorDto;
import com.hospital.dto.PatientDto;
import com.hospital.entity.Appointment;
import com.hospital.service.AdminService;
import com.hospital.service.AppointmentService;
import com.hospital.service.DoctorService;
import com.hospital.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public AdminController(AdminService adminService,
                           DoctorService doctorService,
                           PatientService patientService,
                           AppointmentService appointmentService) {
        this.adminService = adminService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard statistics loaded", stats));
    }

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getAllPatients() {
        List<PatientDto> patients = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success("All registered patients retrieved", patients));
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(ApiResponse.success("All doctors retrieved", doctors));
    }

    @PostMapping("/doctors")
    public ResponseEntity<ApiResponse<DoctorDto>> addDoctor(@RequestBody DoctorDto dto) {
        try {
            DoctorDto created = doctorService.createDoctor(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Doctor added successfully", created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<ApiResponse<DoctorDto>> updateDoctor(@PathVariable Long id, @RequestBody DoctorDto dto) {
        try {
            DoctorDto updated = doctorService.updateDoctor(id, dto);
            return ResponseEntity.ok(ApiResponse.success("Doctor updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok(ApiResponse.success("Doctor deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.success("All appointments retrieved", list));
    }
}
