package com.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hospital Appointment Booking System - Main Application Entry Point
 * Spring Boot 3.2.x / Java 17
 */
@SpringBootApplication
@EnableScheduling
public class HospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  Hospital Appointment Booking System Started!   ");
        System.out.println("  Backend URL : http://localhost:8080            ");
        System.out.println("  Admin Login : admin@hospital.com / admin123    ");
        System.out.println("=================================================");
    }
}
