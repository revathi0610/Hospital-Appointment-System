package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Automatically seeds the database on initial startup if tables are empty.
 * This ensures out-of-the-box demonstration readiness for all 7 intelligent features.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final NotificationRepository notificationRepository;
    private final WaitlistRepository waitlistRepository;
    private final SpecializationKeywordRepository keywordRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           DoctorAvailabilityRepository availabilityRepository,
                           NotificationRepository notificationRepository,
                           WaitlistRepository waitlistRepository,
                           SpecializationKeywordRepository keywordRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.notificationRepository = notificationRepository;
        this.waitlistRepository = waitlistRepository;
        this.keywordRepository = keywordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Specialization Keywords if not already populated (Feature 2)
        seedSpecializationKeywordsIfEmpty();

        if (userRepository.count() > 0) {
            log.info("Database already initialized with existing users. Skipping user seeding.");
            return;
        }

        log.info("Empty database detected. Starting initial data seeding...");

        // 1. Seed System Admin
        User adminUser = new User("System Administrator", "admin@hospital.com",
                passwordEncoder.encode("admin123"), "ADMIN", "+1-555-0100");
        userRepository.save(adminUser);

        // 2. Seed Doctors (with avgConsultationDuration for Feature 4)
        Doctor doc1 = seedDoctor("Dr. Sarah Mitchell", "sarah.mitchell@hospital.com", "doctor123",
                "+1-555-0201", "Cardiology", "MD, FACC - Harvard Medical School", 12, BigDecimal.valueOf(120.00), 20);

        Doctor doc2 = seedDoctor("Dr. Rajesh Sharma", "rajesh.sharma@hospital.com", "doctor123",
                "+1-555-0202", "Neurology", "MBBS, DM (Neurology) - AIIMS", 9, BigDecimal.valueOf(100.00), 15);

        Doctor doc3 = seedDoctor("Dr. Emily Chen", "emily.chen@hospital.com", "doctor123",
                "+1-555-0203", "Pediatrics", "MD (Pediatrics), FAAP - Johns Hopkins", 8, BigDecimal.valueOf(85.00), 15);

        Doctor doc4 = seedDoctor("Dr. Marcus Johnson", "marcus.johnson@hospital.com", "doctor123",
                "+1-555-0204", "Orthopedics", "MS (Ortho), Fellowship Joint Replacement", 14, BigDecimal.valueOf(110.00), 20);

        // 3. Seed Patients
        Patient pat1 = seedPatient("John Doe", "john.doe@patient.com", "patient123",
                "+1-555-0301", LocalDate.of(1992, 5, 14), "MALE", "742 Evergreen Terrace, Springfield");

        Patient pat2 = seedPatient("Alice Smith", "alice.smith@patient.com", "patient123",
                "+1-555-0302", LocalDate.of(1996, 8, 22), "FEMALE", "124 Conch Street, Pacific Ocean");

        Patient pat3 = seedPatient("David Miller", "david.miller@patient.com", "patient123",
                "+1-555-0303", LocalDate.of(1988, 11, 3), "MALE", "221B Baker Street, London");

        // 4. Seed Appointments (with QR tokens for Feature 6 & Marketplace for Feature 3)
        LocalDate today = LocalDate.now();
        appointmentRepository.save(new Appointment(pat1, doc1, today, LocalTime.of(10, 0),
                "Routine heart checkup and blood pressure review", "CONFIRMED", "qr-tok-sarah-1001-c8a7"));

        appointmentRepository.save(new Appointment(pat2, doc3, today, LocalTime.of(11, 0),
                "Child seasonal cough and fever follow-up", "PENDING", "qr-tok-emily-1002-b2f4"));

        appointmentRepository.save(new Appointment(pat3, doc2, today.plusDays(1), LocalTime.of(14, 0),
                "Frequent migraines and dizziness consultation", "CONFIRMED", "qr-tok-rajesh-1003-a9d1"));

        appointmentRepository.save(new Appointment(pat1, doc4, today.plusDays(2), LocalTime.of(9, 30),
                "Knee joint pain after morning jogging", "PENDING", "qr-tok-marcus-1004-e3c8"));

        appointmentRepository.save(new Appointment(pat2, doc1, today.minusDays(7), LocalTime.of(15, 0),
                "ECG evaluation and diet plan consultation", "COMPLETED", "qr-tok-sarah-1005-d1e9"));

        // Feature 3: Seed one RELEASED appointment for demoing the Slot Marketplace
        appointmentRepository.save(new Appointment(pat3, doc1, today.plusDays(1), LocalTime.of(11, 30),
                "Conflict with work schedule - released by David", "RELEASED", "qr-tok-sarah-1006-f4a2"));

        // Feature 5: Seed Sample In-App Notifications
        notificationRepository.save(new Notification(pat1.getUser().getId(), "Appointment Confirmed",
                "Your appointment with Dr. Sarah Mitchell today at 10:00 AM has been confirmed.", "CONFIRMATION"));
        notificationRepository.save(new Notification(pat2.getUser().getId(), "24-Hour Reminder",
                "Reminder: You have an upcoming consultation with Dr. Emily Chen today at 11:00 AM.", "REMINDER"));
        notificationRepository.save(new Notification(pat3.getUser().getId(), "Slot Released to Marketplace",
                "Your slot with Dr. Sarah Mitchell on " + today.plusDays(1) + " at 11:30 AM has been released to the marketplace.", "SLOT_RELEASED"));

        // Feature 3: Seed Sample Waitlist
        waitlistRepository.save(new Waitlist(pat2, doc1, today.plusDays(1), LocalTime.of(11, 30)));

        log.info("Database successfully seeded with Admin, Doctors, Patients, Keywords, Marketplace Slots, and Reminders!");
    }

    private void seedSpecializationKeywordsIfEmpty() {
        if (keywordRepository.count() > 0) return;

        keywordRepository.save(new SpecializationKeyword("Neurology",
                "headache, dizziness, migraine, brain, numbness, seizure, fainting, nerve, memory loss, tremor, vertigo",
                "Specializes in disorders of the nervous system, brain, and spinal cord."));

        keywordRepository.save(new SpecializationKeyword("Cardiology",
                "heart, chest pain, chest discomfort, blood pressure, palpitations, shortness of breath, hypertension, cholesterol, pulse, arrhythmia",
                "Specializes in heart disorders, cardiovascular wellness, and blood pressure."));

        keywordRepository.save(new SpecializationKeyword("Pediatrics",
                "child, baby, pediatric, infant, kid, newborn, vaccination, teething, childhood fever, growth",
                "Specializes in medical care for infants, children, and adolescents."));

        keywordRepository.save(new SpecializationKeyword("Orthopedics",
                "bone, joint, fracture, muscle, arthritis, knee, back pain, spine, shoulder, sprain, ligament, tendon",
                "Specializes in musculoskeletal system injuries, bones, joints, and ligaments."));

        keywordRepository.save(new SpecializationKeyword("Dermatology",
                "skin, acne, rash, itching, allergy, eczema, psoriasis, hair loss, mole, burn, hives",
                "Specializes in conditions affecting skin, hair, and nails."));

        keywordRepository.save(new SpecializationKeyword("Dentistry",
                "tooth, teeth, gum, dental pain, cavity, wisdom tooth, braces, bleeding gums, jaw",
                "Specializes in oral health, dental diseases, and teeth care."));

        keywordRepository.save(new SpecializationKeyword("Ophthalmology",
                "eye, vision, blurry vision, glasses, cataracts, glaucoma, eye strain, redness, cornea",
                "Specializes in eye and visual system disorders."));

        keywordRepository.save(new SpecializationKeyword("Gastroenterology",
                "stomach, digestion, abdominal, acidity, nausea, vomiting, ulcer, diarrhea, constipation, bloating, liver",
                "Specializes in the digestive system and gastrointestinal disorders."));

        keywordRepository.save(new SpecializationKeyword("ENT Specialist",
                "ear, nose, throat, sinus, hearing loss, earache, sore throat, tonsils, nasal congestion, snoring, voice",
                "Specializes in ear, nose, and throat ailments."));

        keywordRepository.save(new SpecializationKeyword("Gynecology",
                "women health, pregnancy, menstrual, periods, pelvic pain, fertility, ovarian, menopause, cervical",
                "Specializes in female reproductive system and prenatal care."));

        log.info("Seeded 10 medical specialization keyword mappings for Symptom -> Specialist Finder.");
    }

    private Doctor seedDoctor(String name, String email, String rawPassword, String phone,
                              String specialization, String qualification, int experience, BigDecimal fee, int duration) {
        User user = new User(name, email, passwordEncoder.encode(rawPassword), "DOCTOR", phone);
        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor(savedUser, specialization, qualification, experience, fee, "AVAILABLE", duration);
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Availability Mon-Fri 09:00 - 17:00
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        for (String day : days) {
            availabilityRepository.save(new DoctorAvailability(savedDoctor, day, LocalTime.of(9, 0), LocalTime.of(17, 0)));
        }

        return savedDoctor;
    }

    private Patient seedPatient(String name, String email, String rawPassword, String phone,
                                LocalDate dob, String gender, String address) {
        User user = new User(name, email, passwordEncoder.encode(rawPassword), "PATIENT", phone);
        User savedUser = userRepository.save(user);

        Patient patient = new Patient(savedUser, dob, gender, address);
        return patientRepository.save(patient);
    }
}
