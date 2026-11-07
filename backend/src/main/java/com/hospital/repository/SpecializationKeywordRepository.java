package com.hospital.repository;

import com.hospital.entity.SpecializationKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationKeywordRepository extends JpaRepository<SpecializationKeyword, Long> {

    Optional<SpecializationKeyword> findBySpecializationIgnoreCase(String specialization);

    List<SpecializationKeyword> findAllByOrderBySpecializationAsc();

    boolean existsBySpecializationIgnoreCase(String specialization);
}
