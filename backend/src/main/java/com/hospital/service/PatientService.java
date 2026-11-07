package com.hospital.service;

import com.hospital.dto.PatientDto;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public PatientService(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));
        return mapToDto(patient);
    }

    public PatientDto getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found for User ID: " + userId));
        return mapToDto(patient);
    }

    @Transactional
    public PatientDto updatePatientProfile(Long id, PatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));

        User user = patient.getUser();
        if (dto.getName() != null) user.setName(dto.getName().trim());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone().trim());
        userRepository.save(user);

        if (dto.getDateOfBirth() != null) patient.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getGender() != null) patient.setGender(dto.getGender());
        if (dto.getAddress() != null) patient.setAddress(dto.getAddress());

        return mapToDto(patientRepository.save(patient));
    }

    private PatientDto mapToDto(Patient patient) {
        User user = patient.getUser();
        return new PatientDto(
                patient.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getName() : "Unknown",
                user != null ? user.getEmail() : "",
                user != null ? user.getPhone() : "",
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getAddress(),
                user != null ? user.getCreatedAt() : null
        );
    }
}
