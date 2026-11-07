package com.hospital.service;

import com.hospital.dto.DashboardStatsDto;
import com.hospital.dto.DoctorDashboardStatsDto;
import com.hospital.dto.PatientDto;
import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public AdminService(AppointmentRepository appointmentRepository,
                        PatientRepository patientRepository,
                        DoctorRepository doctorRepository,
                        UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        LocalDate today = LocalDate.now();

        long totalAppointments = appointmentRepository.count();
        long todayAppointments = appointmentRepository.countByAppointmentDate(today);
        long totalPatients = patientRepository.count();
        long availableDoctors = doctorRepository.countByAvailabilityStatus("AVAILABLE");
        long totalDoctors = doctorRepository.count();

        DashboardStatsDto stats = new DashboardStatsDto(
                totalAppointments,
                todayAppointments,
                totalPatients,
                availableDoctors,
                totalDoctors
        );

        List<Appointment> upcoming = appointmentRepository.findUpcomingAppointments(today);
        stats.setUpcomingAppointments(upcoming.stream().limit(8).collect(Collectors.toList()));

        List<PatientDto> recentPatients = patientRepository.findTop10ByOrderByIdDesc().stream()
                .map(p -> new PatientDto(
                        p.getId(),
                        p.getUser() != null ? p.getUser().getId() : null,
                        p.getUser() != null ? p.getUser().getName() : "Unknown",
                        p.getUser() != null ? p.getUser().getEmail() : "",
                        p.getUser() != null ? p.getUser().getPhone() : "",
                        p.getDateOfBirth(),
                        p.getGender(),
                        p.getAddress(),
                        p.getUser() != null ? p.getUser().getCreatedAt() : null
                ))
                .limit(5)
                .collect(Collectors.toList());
        stats.setRecentPatients(recentPatients);

        return stats;
    }

    public DoctorDashboardStatsDto getDoctorDashboardStats(Long doctorId) {
        LocalDate today = LocalDate.now();

        long todayAppointments = appointmentRepository.countByDoctorIdAndAppointmentDate(doctorId, today);
        long upcomingAppointments = appointmentRepository.findUpcomingAppointmentsByDoctor(doctorId, today).size();
        long totalPatients = appointmentRepository.countDistinctPatientsByDoctorId(doctorId);
        long pendingAppointments = appointmentRepository.countByDoctorIdAndStatus(doctorId, "PENDING");

        return new DoctorDashboardStatsDto(
                todayAppointments,
                upcomingAppointments,
                totalPatients,
                pendingAppointments
        );
    }
}
