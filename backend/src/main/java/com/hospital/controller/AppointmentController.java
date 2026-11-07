package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentStatusUpdateRequest;
import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Appointment>> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = appointmentService.bookAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Appointment booked successfully!", appointment));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to book appointment: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments() {
        List<Appointment> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.success("Appointments retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(ApiResponse.success("Appointment found", appointment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPatientAppointments(@PathVariable Long patientId) {
        List<Appointment> list = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient appointments retrieved", list));
    }

    @GetMapping("/patient/{patientId}/upcoming")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPatientUpcomingAppointments(@PathVariable Long patientId) {
        List<Appointment> list = appointmentService.getUpcomingAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Upcoming appointments retrieved", list));
    }

    @GetMapping("/patient/{patientId}/past")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPatientPastAppointments(@PathVariable Long patientId) {
        List<Appointment> list = appointmentService.getPastAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Past appointments retrieved", list));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getDoctorAppointments(@PathVariable Long doctorId) {
        List<Appointment> list = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor appointments retrieved", list));
    }

    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<ApiResponse<List<Appointment>>> getDoctorTodayAppointments(@PathVariable Long doctorId) {
        List<Appointment> list = appointmentService.getTodayAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Today's appointments retrieved", list));
    }

    @GetMapping("/doctor/{doctorId}/upcoming")
    public ResponseEntity<ApiResponse<List<Appointment>>> getDoctorUpcomingAppointments(@PathVariable Long doctorId) {
        List<Appointment> list = appointmentService.getUpcomingAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor upcoming appointments retrieved", list));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Appointment>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        try {
            Appointment updated = appointmentService.updateAppointmentStatus(id, request.getStatus());
            return ResponseEntity.ok(ApiResponse.success("Appointment status updated to " + request.getStatus(), updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointmentViaPut(@PathVariable Long id) {
        try {
            Appointment cancelled = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", cancelled));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointmentViaDelete(@PathVariable Long id) {
        try {
            Appointment cancelled = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", cancelled));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
