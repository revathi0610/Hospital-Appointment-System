# Hospital Appointment Booking System 🏥

A complete, production-ready, full-stack Hospital Appointment Management and Scheduling System with **7 Intelligent Healthcare Features**, designed for patients, doctors, and healthcare administrators. Built with **Java 17**, **Spring Boot 3.2.5**, **Spring Data JPA**, **MySQL**, and a modern **HTML5/CSS3/JavaScript (ES6+)** responsive frontend.

Designed with clean architecture and clear separation of concerns, making it an ideal 3rd-year CSE academic capstone, minor/major project, and resume-ready portfolio demonstration.

---

## 🌟 Table of Contents
1. [Project Overview](#-project-overview)
2. [7 Intelligent Smart Features](#-7-intelligent-smart-features)
   - [Feature 1: AI Hospital Assistant Chatbot](#feature-1-ai-hospital-assistant-chatbot)
   - [Feature 2: Symptom → Specialist Finder](#feature-2-symptom--specialist-finder)
   - [Feature 3: Appointment Swap / Slot Marketplace & Waitlist](#feature-3-appointment-swap--slot-marketplace--waitlist)
   - [Feature 4: Smart Slot & Waiting-Time Estimation](#feature-4-smart-slot--waiting-time-estimation)
   - [Feature 5: Automatic Appointment Reminders](#feature-5-automatic-appointment-reminders)
   - [Feature 6: QR Code Digital Appointment Card & Verification](#feature-6-qr-code-digital-appointment-card--verification)
   - [Feature 7: Doctor & Hospital Operational Analytics](#feature-7-doctor--hospital-operational-analytics)
3. [Core Role Features (Patient, Doctor, Admin)](#-core-role-features)
4. [Technology Stack](#-technology-stack)
5. [System Architecture](#-system-architecture)
6. [Database Schema](#-database-schema)
7. [Step-by-Step Setup & Running Guide](#-step-by-step-setup--running-guide)
8. [REST API Documentation](#-rest-api-documentation)
9. [Sample Test Credentials](#-sample-test-credentials)
10. [Automated Testing & Verification](#-automated-testing--verification)

---

## 📖 Project Overview

Managing clinical appointments via phone calls or manual registers leads to scheduling conflicts, missed consultations, long waiting queues, and administrative burnout. 

The **Hospital Appointment Booking System** solves this by combining standard appointment booking with an intelligent clinical triage and hospital operations suite:
- **Zero Double-Booking**: Guaranteed slot collision prevention for both doctors and patients.
- **Dynamic Queue & Waiting-Time Estimation**: Computes live waiting durations from patient queues and doctor pace.
- **Slot Marketplace & Active Waitlists**: Allows patients to release appointments and automatically notifies waitlisted users.
- **Secure QR Passes**: Generates contactless check-in passes with tamper-proof UUID tokens.
- **AI Triage Chatbot**: Guides patients with symptom routing while strictly enforcing medical safety guardrails.
- **100% Offline / Free Demo Ready**: Operates completely out of the box with zero required paid API subscriptions (supports optional external LLM via `.env.example`).

---

## 🧠 7 Intelligent Smart Features

### Feature 1: AI Hospital Assistant Chatbot
- **Interactive Floating Widget**: Accessible from all patient pages via a sleek bottom-right floating bubble.
- **Safety Guardrails & Disclaimers**: Strictly refuses to prescribe medicines or issue diagnostic claims. Displays educational guidance: *"Based on the information you entered, you may consider consulting a suitable specialist. This system does not diagnose medical conditions."*
- **Emergency Triage**: Detects urgent red-flag triggers (e.g., chest pain, shortness of breath, heavy bleeding, stroke) and immediately advises contacting emergency services (911/112) with instant call actions.
- **Personalized Appointment Retrieval**: Patients can ask *"What time is my appointment?"* and the assistant securely retrieves their upcoming visit from the database.
- **Action Buttons & Navigation**: Provides 1-click interactive action chips to view specialists, book slots, release appointments, or check passes.
- **Dual Engine**: Features built-in rule/intent clinical triage + optional Gemini/LLM integration via `AI_API_KEY`.

### Feature 2: Symptom → Specialist Finder
- **Intelligent Keyword Matching**: Analyzes free-form symptom descriptions (e.g., *"frequent headaches and dizziness"*) against clinical keyword rules stored in the database.
- **Department Routing**: Recommends appropriate medical departments (e.g., Neurology, Cardiology, Dermatology, Pediatrics) with match confidence badges (`HIGH`, `MEDIUM`, `LOW`).
- **Direct Specialist Booking**: Displays matching doctors within the recommended department with 1-click *"Book Now"* buttons.
- **Mandatory Medical Disclaimer**: Prominently highlights legal disclaimer regarding non-diagnostic purpose.
- **Admin Keyword Rule Manager**: Administrators can add, edit, or remove symptom keyword mappings directly from the admin portal.

### Feature 3: Appointment Swap / Slot Marketplace & Waitlist
- **1-Click Slot Release**: Patients who cannot attend can release their appointment (`RELEASED` status) to the hospital marketplace instead of letting the slot go unused.
- **Slot Marketplace**: An open board of newly opened slots where other patients can immediately claim a visit.
- **Race-Condition Safe Slot Claiming**: Concurrency-protected database transaction ensures only one patient can claim an open slot, preventing race conditions.
- **Doctor Waitlists**: If a doctor has no available slots on a specific date, patients can join the priority waitlist.
- **Automatic Waitlist Alerts**: Releasing an appointment automatically scans the doctor's active waitlist and sends in-app notifications to waiting patients.

### Feature 4: Smart Slot & Waiting-Time Estimation
- **Queue-Aware Waiting Times**: Calculates live estimated wait times using the formula:
  $$\text{Estimated Wait (minutes)} = \text{Patients Ahead in Queue} \times \text{Doctor's Average Consultation Duration}$$
- **Doctor Pace Customization**: Doctors can configure their average consultation duration (default: 15 minutes, configurable between 5 and 120 minutes).
- **Smart Slot Recommendations**: Color-coded badges on the booking screen highlight:
  - ⭐️ **Shortest Wait / Earliest Slot** (0 mins wait)
  - 🔵 **Low Wait Slot** (~15 mins wait)
  - ⚡ **Marketplace Released Slot** (Instant booking)

### Feature 5: Automatic Appointment Reminders
- **Background Scheduled Job**: Powered by Spring's `@Scheduled` background worker, scanning every 5 minutes.
- **24-Hour Advance Reminder**: Sends alerts for appointments scheduled tomorrow.
- **1-Hour Urgent Reminder**: Sends arrival notifications when an appointment is within 90 minutes.
- **In-App Notification Drawer**: Interactive bell icon in the topbar with a real-time unread badge count, dropdown alerts, and "Mark all read" capability.

### Feature 6: QR Code Digital Appointment Card & Verification
- **Digital Boarding Pass**: Every confirmed visit generates a digital appointment card formatted like an official hospital check-in pass.
- **Secure UUID Tokens**: Encodes a non-sensitive verification token (`qrToken`) avoiding exposure of private medical diagnoses.
- **Print & Save**: Optimized for standard browser printing (`window.print()`).
- **Live Queue Counter**: Displays real-time estimated waiting time directly on the pass.
- **Hospital Reception Verifier**: Doctor and Admin portals include a quick QR token verification desk to check in patients instantly.

### Feature 7: Doctor & Hospital Operational Analytics
- **Doctor Analytics**:
  - Total consultations (today, this week, this month, all-time)
  - Completion Rate (%) & Cancellation Rate (%)
  - Average consultation duration setting
  - Workload distribution across days of the week (Monday – Saturday)
- **Hospital Executive Intelligence (Admin)**:
  - Hospital Capacity Utilization Rate (%)
  - Average hospital waiting time across all clinics
  - Department workload distribution (appointments by specialization)
  - 14-day daily appointment volume trend activity

---

## 👥 Core Role Features

### 1. Patient Portal
- Register, login, and update personal profile.
- Browse all doctors or filter by specialty.
- Book appointments using the interactive Smart Slot Picker.
- View upcoming and previous appointment records.
- Release appointments to the Slot Marketplace.
- Join and manage doctor waitlists.
- View and print digital QR appointment passes.
- Interact with the AI Hospital Assistant Chatbot.

### 2. Doctor Portal
- Secure doctor authentication.
- View today's schedule and upcoming appointments.
- Accept (`PENDING` ➔ `CONFIRMED`), Complete (`CONFIRMED` ➔ `COMPLETED`), or Cancel visits.
- View full patient demographic and contact dossiers.
- Configure average consultation duration.
- View clinical performance metrics and workload charts.
- Verify patient QR passes at the clinic desk.

### 3. Admin Portal
- Executive KPI overview of hospital operations.
- Doctor roster management: Add new doctors, set fees, toggle availability, or delete profiles.
- Patient directory browsing.
- Manage all hospital appointments and override statuses.
- Configure symptom keyword rules for the AI triage engine.
- Verify appointment QR passes at the central reception desk.

---

## 🛠 Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Backend** | Java 17 (LTS) | Clean OOP, Spring Boot 3.2.5 |
| **Frameworks** | Spring Boot, Spring Web, Spring Data JPA, Spring Security | RESTful API, ORM, BCrypt |
| **Scheduling** | `@EnableScheduling` | Automated appointment reminders runner |
| **Database** | MySQL 8.0+ | Relational schema with foreign keys and indexes |
| **Build Tool** | Apache Maven 3.9+ | Dependency and build lifecycle management |
| **Frontend** | HTML5, CSS3, JavaScript (ES6+) | Vanilla responsive UI (No Node.js or npm required) |
| **Testing** | JUnit 5, Spring Boot Test, H2 In-Memory DB | Automated integration test suite |

---

## 🏛 System Architecture

```
[ Frontend: HTML5 / CSS3 / Vanilla JS ]
   │
   │  REST API Calls (Fetch API with JSON)
   ▼
[ Spring Boot Controller Layer ]
   ├── AuthController              (/api/auth/**)
   ├── PatientController           (/api/patients/**)
   ├── DoctorController            (/api/doctors/**)
   ├── AppointmentController       (/api/appointments/**)
   ├── AppointmentSwapController   (/api/appointments/**/release, claim, /api/waitlist/**)
   ├── WaitingTimeController       (/api/appointments/**/waiting-time, /api/doctors/**/smart-slots)
   ├── AppointmentQrController     (/api/appointments/**/card, verify-qr)
   ├── SpecialistController        (/api/specialists/**)
   ├── ChatbotController           (/api/chatbot/message)
   ├── NotificationController      (/api/notifications/**)
   └── AnalyticsController          (/api/doctors/**/analytics, /api/admin/analytics)
   │
[ Service & Scheduler Layer ]
   ├── AppointmentService / AppointmentSwapService / WaitingTimeService
   ├── SpecialistFinderService / ChatbotService / NotificationService
   ├── AppointmentReminderScheduler (@Scheduled) / AnalyticsService / QrCodeService
   │
[ Repository Layer (Spring Data JPA) ]
   └── 8 Interfaces querying MySQL / Hibernate
   │
[ MySQL Database (hospital_db) ]
   ├── users, doctors, patients, doctor_availability, appointments
   ├── notifications, waitlist, specialization_keywords
```

---

## 🗄 Database Schema

The database consists of **8 interconnected tables**:

1. `users`: Stores login credentials (`id`, `name`, `email`, `password` [BCrypt], `role`, `phone`, `created_at`).
2. `doctors`: Medical credentials (`id`, `user_id`, `specialization`, `qualification`, `experience`, `consultation_fee`, `availability_status`, `avg_consultation_duration`, `department`, `consultation_room`).
3. `patients`: Patient profiles (`id`, `user_id`, `date_of_birth`, `gender`, `address`).
4. `doctor_availability`: Weekly working hours (`doctor_id`, `day_of_week`, `start_time`, `end_time`).
5. `appointments`: Visits (`id`, `patient_id`, `doctor_id`, `appointment_date`, `appointment_time`, `status`, `reason`, `qr_token`, `created_at`).
6. `notifications`: In-app alerts (`id`, `user_id`, `title`, `message`, `type`, `appointment_id`, `is_read`, `created_at`).
7. `waitlist`: Priority queues (`id`, `patient_id`, `doctor_id`, `preferred_date`, `preferred_time`, `status`, `notes`, `created_at`).
8. `specialization_keywords`: Clinical triage rules (`id`, `specialization`, `department`, `keywords`, `description`).

---

## 🚀 Step-by-Step Setup & Running Guide

### Step 1: MySQL Database Setup
Log into MySQL and run the schema setup:
```sql
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;
```
Import `database/schema.sql` (and optionally `database/sample_data.sql`):
```bash
mysql -u root -p hospital_db < database/schema.sql
mysql -u root -p hospital_db < database/sample_data.sql
```
*(Note: If the database is blank, Spring Boot's built-in `DataInitializer` will automatically seed all default accounts, doctors, and sample appointments on first startup!)*

### Step 2: Configure MySQL Credentials
Check `backend/src/main/resources/application.properties` and verify your MySQL password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YourPasswordHere
```

### Step 3: Run Spring Boot Backend
Open a terminal in the `backend/` directory:
```bash
mvn clean spring-boot:run
```
The backend server will launch at `http://localhost:8080`.

### Step 4: Launch Frontend
Open `frontend/index.html` directly in any web browser, or serve it via VS Code Live Server or python:
```bash
cd frontend
python -m http.server 3000
```
Open `http://localhost:3000` in your browser.

---

## 🔑 Sample Test Credentials

| Role | Email | Password | Details |
|---|---|---|---|
| **Admin** | `admin@hospital.com` | `admin123` | Hospital Executive Dashboard |
| **Doctor** | `sarah.johnson@hospital.com` | `doctor123` | Cardiologist |
| **Doctor** | `david.chen@hospital.com` | `doctor123` | Neurologist |
| **Doctor** | `emily.rodriguez@hospital.com` | `doctor123` | Pediatrician |
| **Doctor** | `michael.brown@hospital.com` | `doctor123` | Orthopedic Surgeon |
| **Patient** | `john.doe@patient.com` | `patient123` | Sample Patient Profile |
| **Patient** | `jane.smith@patient.com` | `patient123` | Sample Patient Profile |

---

## 🧪 Automated Testing & Verification

Run the complete integration and unit test suite:
```bash
cd backend
mvn test
```
The test suite validates all 7 features:
- Chatbot safety guardrails & emergency detection
- Symptom keyword analysis & department matching
- Slot release & race-condition safe marketplace claiming
- Live queue waiting-time estimation
- Automated appointment reminder scheduler scans
- QR Pass UUID generation & reception verification
- Doctor & Admin operational analytics computation

---

## 📄 License & Academic Note

This project is open-source under the MIT License. Created as a robust, beginner-friendly full-stack reference implementation for CSE students and software engineering pair programming.
