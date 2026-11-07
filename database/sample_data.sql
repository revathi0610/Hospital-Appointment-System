-- =======================================================
-- Hospital Appointment Booking System - Sample Data
-- Enhanced with Sample Specialization Keywords, Reminders, and Released Slots
-- =======================================================

USE hospital_db;

-- 1. Insert Users (Admin, 4 Doctors, 3 Patients)
INSERT INTO users (id, name, email, password, role, phone, created_at) VALUES
(1, 'System Administrator', 'admin@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'ADMIN', '+1-555-0100', NOW()),

-- Doctors
(2, 'Dr. Sarah Mitchell', 'sarah.mitchell@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'DOCTOR', '+1-555-0201', NOW()),
(3, 'Dr. Rajesh Sharma', 'rajesh.sharma@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'DOCTOR', '+1-555-0202', NOW()),
(4, 'Dr. Emily Chen', 'emily.chen@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'DOCTOR', '+1-555-0203', NOW()),
(5, 'Dr. Marcus Johnson', 'marcus.johnson@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'DOCTOR', '+1-555-0204', NOW()),
(6, 'Dr. Lisa Brown', 'lisa.brown@hospital.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'DOCTOR', '+1-555-0205', NOW()),


-- Patients
(6, 'John Doe', 'john.doe@patient.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'PATIENT', '+1-555-0301', NOW()),
(7, 'Alice Smith', 'alice.smith@patient.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'PATIENT', '+1-555-0302', NOW()),
(8, 'David Miller', 'david.miller@patient.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StQE/56KvnprC', 'PATIENT', '+1-555-0303', NOW());

-- 2. Insert Doctors Profile (Includes avg_consultation_duration for Smart Waiting Time)
INSERT INTO doctors (id, user_id, specialization, qualification, experience, consultation_fee, availability_status, avg_consultation_duration) VALUES
(1, 2, 'Cardiology', 'MD, FACC - Harvard Medical School', 12, 120.00, 'AVAILABLE', 20),
(2, 3, 'Neurology', 'MBBS, DM (Neurology) - AIIMS', 9, 100.00, 'AVAILABLE', 15),
(3, 4, 'Pediatrics', 'MD (Pediatrics), FAAP - Johns Hopkins', 8, 85.00, 'AVAILABLE', 15),
(4, 5, 'Orthopedics', 'MS (Ortho), Fellowship in Joint Replacement', 14, 110.00, 'AVAILABLE', 20);

  

-- 3. Insert Patients Profile
INSERT INTO patients (id, user_id, date_of_birth, gender, address) VALUES
(1, 6, '1992-05-14', 'MALE', '742 Evergreen Terrace, Springfield'),
(2, 7, '1996-08-22', 'FEMALE', '124 Conch Street, Bikini Bottom'),
(3, 8, '1988-11-03', 'MALE', '221B Baker Street, London');

-- 4. Insert Doctor Weekly Availability Schedules
INSERT INTO doctor_availability (doctor_id, day_of_week, start_time, end_time) VALUES
(1, 'MONDAY', '09:00:00', '17:00:00'),
(1, 'TUESDAY', '09:00:00', '17:00:00'),
(1, 'WEDNESDAY', '09:00:00', '17:00:00'),
(1, 'THURSDAY', '09:00:00', '17:00:00'),
(1, 'FRIDAY', '09:00:00', '16:00:00'),

(2, 'MONDAY', '10:00:00', '18:00:00'),
(2, 'WEDNESDAY', '10:00:00', '18:00:00'),
(2, 'FRIDAY', '10:00:00', '18:00:00'),
(2, 'SATURDAY', '10:00:00', '14:00:00'),

(3, 'TUESDAY', '08:30:00', '15:30:00'),
(3, 'THURSDAY', '08:30:00', '15:30:00'),
(3, 'SATURDAY', '08:30:00', '14:00:00'),

(4, 'MONDAY', '09:00:00', '16:00:00'),
(4, 'TUESDAY', '09:00:00', '16:00:00'),
(4, 'WEDNESDAY', '09:00:00', '16:00:00'),
(4, 'THURSDAY', '09:00:00', '16:00:00');

-- 5. Insert Sample Appointments with QR Tokens and Released Marketplace Slots
INSERT INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, reason, status, qr_token, created_at) VALUES
(1, 1, 1, CURDATE(), '10:00:00', 'Routine heart checkup and blood pressure review', 'CONFIRMED', 'qr-tok-sarah-1001-c8a7', NOW()),
(2, 2, 3, CURDATE(), '11:00:00', 'Child seasonal cough and fever follow-up', 'PENDING', 'qr-tok-emily-1002-b2f4', NOW()),
(3, 3, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', 'Frequent migraines and dizziness consultation', 'CONFIRMED', 'qr-tok-rajesh-1003-a9d1', NOW()),
(4, 1, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:30:00', 'Knee joint pain after morning jogging', 'PENDING', 'qr-tok-marcus-1004-e3c8', NOW()),
(5, 2, 1, DATE_SUB(CURDATE(), INTERVAL 7 DAY), '15:00:00', 'ECG evaluation and diet plan consultation', 'COMPLETED', 'qr-tok-sarah-1005-d1e9', NOW()),
-- Released slot available in Marketplace for other patients to claim:
(6, 3, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:30:00', 'Schedule conflict - released by David', 'RELEASED', 'qr-tok-sarah-1006-f4a2', NOW());

-- 6. Insert Specialization Keywords Dictionary (Feature 2: Symptom -> Specialist Finder)
INSERT INTO specialization_keywords (specialization, keywords, description) VALUES
('Neurology', 'headache, dizziness, migraine, brain, numbness, seizure, fainting, nerve, memory loss, tremor, vertigo', 'Specializes in disorders of the nervous system, brain, and spinal cord.'),
('Cardiology', 'heart, chest pain, chest discomfort, blood pressure, palpitations, shortness of breath, hypertension, cholesterol, pulse, arrhythmia', 'Specializes in heart disorders, cardiovascular wellness, and blood pressure.'),
('Pediatrics', 'child, baby, pediatric, infant, kid, newborn, vaccination, teething, childhood fever, growth', 'Specializes in medical care for infants, children, and adolescents.'),
('Orthopedics', 'bone, joint, fracture, muscle, arthritis, knee, back pain, spine, shoulder, sprain, ligament, tendon', 'Specializes in musculoskeletal system injuries, bones, joints, and ligaments.'),
('Dermatology', 'skin, acne, rash, itching, allergy, eczema, psoriasis, hair loss, mole, burn, hives', 'Specializes in conditions affecting skin, hair, and nails.'),
('Dentistry', 'tooth, teeth, gum, dental pain, cavity, wisdom tooth, braces, bleeding gums, jaw', 'Specializes in oral health, dental diseases, and teeth care.'),
('Ophthalmology', 'eye, vision, blurry vision, glasses, cataracts, glaucoma, eye strain, redness, cornea', 'Specializes in eye and visual system disorders.'),
('Gastroenterology', 'stomach, digestion, abdominal, acidity, nausea, vomiting, ulcer, diarrhea, constipation, bloating, liver', 'Specializes in the digestive system and gastrointestinal disorders.'),
('ENT Specialist', 'ear, nose, throat, sinus, hearing loss, earache, sore throat, tonsils, nasal congestion, snoring, voice', 'Specializes in ear, nose, and throat ailments.'),
('Gynecology', 'women health, pregnancy, menstrual, periods, pelvic pain, fertility, ovarian, menopause, cervical', 'Specializes in female reproductive system and prenatal care.');

-- 7. Insert Sample In-App Notifications (Feature 5)
INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES
(6, 'Appointment Confirmed', 'Your appointment with Dr. Sarah Mitchell on today at 10:00 AM has been confirmed.', 'CONFIRMATION', FALSE, NOW()),
(7, '24-Hour Reminder', 'Reminder: You have an upcoming appointment with Dr. Emily Chen on today at 11:00 AM.', 'REMINDER', FALSE, NOW()),
(8, 'Slot Available in Marketplace', 'A slot with Dr. Sarah Mitchell on tomorrow at 11:30 AM has been released and is available to claim.', 'SLOT_RELEASED', FALSE, NOW());

-- 8. Insert Sample Waitlist Entry (Feature 3)
INSERT INTO waitlist (patient_id, doctor_id, preferred_date, preferred_time, status, created_at) VALUES
(2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:30:00', 'NOTIFIED', NOW());
