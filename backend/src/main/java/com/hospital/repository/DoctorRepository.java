package com.hospital.repository;

import com.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);

    List<Doctor> findBySpecializationIgnoreCase(String specialization);

    List<Doctor> findByAvailabilityStatus(String status);

    long countByAvailabilityStatus(String status);

    @Query("SELECT d FROM Doctor d WHERE " +
           "(:specialization IS NULL OR LOWER(d.specialization) = LOWER(:specialization)) AND " +
           "(:query IS NULL OR LOWER(d.user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Doctor> searchDoctors(@Param("specialization") String specialization, @Param("query") String query);

    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization ASC")
    List<String> findDistinctSpecializations();
}
