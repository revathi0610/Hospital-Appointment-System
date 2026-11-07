package com.hospital.repository;

import com.hospital.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    List<Waitlist> findByDoctorIdAndPreferredDateAndStatusOrderByCreatedAtAsc(Long doctorId, LocalDate preferredDate, String status);

    List<Waitlist> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    boolean existsByPatientIdAndDoctorIdAndPreferredDateAndStatus(Long patientId, Long doctorId, LocalDate preferredDate, String status);

    long countByDoctorIdAndPreferredDateAndStatus(Long doctorId, LocalDate preferredDate, String status);
}
