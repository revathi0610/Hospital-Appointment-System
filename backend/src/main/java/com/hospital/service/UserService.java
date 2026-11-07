package com.hospital.service;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.entity.User;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse registerPatient(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("An account with this email already exists: " + request.getEmail());
        }

        // Create base User with BCrypt hashed password
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("PATIENT");
        user.setPhone(request.getPhone());
        User savedUser = userRepository.save(user);

        // Create Patient profile
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());
        Patient savedPatient = patientRepository.save(patient);

        String token = "patient-session-" + UUID.randomUUID();
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedPatient.getId(),
                null,
                savedUser.getPhone(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        Long patientId = null;
        Long doctorId = null;

        if ("PATIENT".equalsIgnoreCase(user.getRole())) {
            Optional<Patient> patientOpt = patientRepository.findByUserId(user.getId());
            if (patientOpt.isPresent()) {
                patientId = patientOpt.get().getId();
            }
        } else if ("DOCTOR".equalsIgnoreCase(user.getRole())) {
            Optional<Doctor> doctorOpt = doctorRepository.findByUserId(user.getId());
            if (doctorOpt.isPresent()) {
                doctorId = doctorOpt.get().getId();
            }
        }

        String token = user.getRole().toLowerCase() + "-session-" + UUID.randomUUID();
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                patientId,
                doctorId,
                user.getPhone(),
                token
        );
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
