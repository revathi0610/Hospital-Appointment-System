-- =======================================================
-- Hospital Appointment Booking System - Database Schema
-- Enhanced with 7 Smart Features (Compatible with MySQL 8.0+)
-- =======================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS waitlist;
DROP TABLE IF EXISTS specialization_keywords;
DROP TABLE IF EXISTS doctor_availability;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

-- -------------------------------------------------------
-- 1. Table: users
-- Core authentication details for PATIENT, DOCTOR, ADMIN
-- -------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'PATIENT', 'DOCTOR', 'ADMIN'
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 2. Table: patients
-- Patient medical profile details (1-to-1 with users)
-- -------------------------------------------------------
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(10), -- 'MALE', 'FEMALE', 'OTHER'
    address VARCHAR(255),
    CONSTRAINT fk_patient_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 3. Table: doctors
-- Doctor credentials and consultation configurations
-- Includes avg_consultation_duration for Smart Wait Time Estimation
-- -------------------------------------------------------
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(100) NOT NULL,
    experience INT NOT NULL DEFAULT 0, -- in years
    consultation_fee DECIMAL(10, 2) NOT NULL DEFAULT 50.00,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'UNAVAILABLE', 'ON_LEAVE'
    avg_consultation_duration INT NOT NULL DEFAULT 15, -- in minutes (Feature 4: Smart Waiting Time)
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_doctor_specialization (specialization)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 4. Table: appointments
-- Scheduled, released, or swapped patient appointments
-- Includes qr_token for Secure Digital Appointment Passes
-- -------------------------------------------------------
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', 
    -- Statuses: 'PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'RELEASED', 'SWAPPED', 'WAITLISTED'
    qr_token VARCHAR(64) UNIQUE, -- Feature 6: QR Code token
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_doctor FOREIGN KEY (doctor_id) 
        REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_appointment_doctor_date (doctor_id, appointment_date),
    INDEX idx_appointment_patient_date (patient_id, appointment_date),
    INDEX idx_appointment_qr_token (qr_token),
    INDEX idx_appointment_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 5. Table: doctor_availability
-- Weekly working hours per weekday
-- -------------------------------------------------------
CREATE TABLE doctor_availability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL, -- 'MONDAY', 'TUESDAY', 'WEDNESDAY', etc.
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_availability_doctor FOREIGN KEY (doctor_id) 
        REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_availability_doctor_day (doctor_id, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 6. Table: notifications
-- In-app notifications & automated appointment reminders (Feature 5)
-- -------------------------------------------------------
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'GENERAL', -- 'REMINDER', 'CONFIRMATION', 'CANCELLATION', 'SLOT_RELEASED', 'WAITLIST'
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notification_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 7. Table: waitlist
-- Patient waitlist queue for fully booked slots (Feature 3)
-- -------------------------------------------------------
CREATE TABLE waitlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    preferred_date DATE NOT NULL,
    preferred_time TIME,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING', -- 'WAITING', 'NOTIFIED', 'CLAIMED', 'CANCELLED', 'EXPIRED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_waitlist_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_waitlist_doctor FOREIGN KEY (doctor_id) 
        REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_waitlist_doc_date_status (doctor_id, preferred_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------
-- 8. Table: specialization_keywords
-- Dictionary mapping symptoms/keywords to medical departments (Feature 2)
-- -------------------------------------------------------
CREATE TABLE specialization_keywords (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    specialization VARCHAR(100) NOT NULL UNIQUE,
    keywords TEXT NOT NULL,
    description TEXT,
    INDEX idx_keyword_specialization (specialization)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
