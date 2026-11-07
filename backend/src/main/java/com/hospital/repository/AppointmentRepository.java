package com.hospital.repository;

import com.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(Long doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDateOrderByAppointmentTimeAsc(Long doctorId, LocalDate appointmentDate);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(Long doctorId, LocalDate appointmentDate, String excludedStatus);

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String excludedStatus);

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long patientId, LocalDate appointmentDate, LocalTime appointmentTime, String excludedStatus);

    long countByAppointmentDate(LocalDate date);

    long countByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    long countByDoctorIdAndStatus(Long doctorId, String status);

    @Query("SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a WHERE a.doctor.id = :doctorId")
    long countDistinctPatientsByDoctorId(@Param("doctorId") Long doctorId);

    List<Appointment> findTop10ByOrderByAppointmentDateDescAppointmentTimeDesc();

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate >= :today AND a.status != 'CANCELLED' ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointments(@Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :today AND a.status != 'CANCELLED' ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId, @Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDate >= :today AND a.status != 'CANCELLED' ORDER BY a.appointmentDate ASC, a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND (a.appointmentDate < :today OR a.status = 'COMPLETED' OR a.status = 'CANCELLED') ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> findPastAppointmentsByPatient(@Param("patientId") Long patientId, @Param("today") LocalDate today);

    // Feature 6: Secure QR Code Lookup
    java.util.Optional<Appointment> findByQrToken(String qrToken);

    // Feature 3: Slot Marketplace (Released Appointments)
    List<Appointment> findByStatusOrderByAppointmentDateAscAppointmentTimeAsc(String status);

    // Feature 4: Smart Waiting Time Queue Calculation
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status IN :statuses ORDER BY a.appointmentTime ASC")
    List<Appointment> findActiveQueue(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("statuses") java.util.Collection<String> statuses);

    // Feature 5: Upcoming Reminders Range Query
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :date AND a.status IN ('PENDING', 'CONFIRMED')")
    List<Appointment> findAppointmentsForReminders(@Param("date") LocalDate date);

    // Feature 7: Analytics queries
    long countByStatus(String status);

    long countByDoctorId(Long doctorId);

    List<Appointment> findByAppointmentDateBetween(LocalDate start, LocalDate end);

    List<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDate start, LocalDate end);
}
