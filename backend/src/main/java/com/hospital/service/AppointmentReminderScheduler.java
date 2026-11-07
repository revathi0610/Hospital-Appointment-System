package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class AppointmentReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderScheduler.class);

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    public AppointmentReminderScheduler(AppointmentRepository appointmentRepository,
                                        NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
    }

    // Runs every 5 minutes (300,000 ms) with initial delay of 10 seconds
    @Scheduled(fixedRate = 300000, initialDelay = 10000)
    @Transactional
    public void runAppointmentReminders() {
        log.info("Running automated appointment reminders scan...");
        scanTomorrowReminders();
        scanUpcomingTodayReminders();
    }

    // Can be invoked manually from controller or tests
    @Transactional
    public int triggerReminderScan() {
        int count = 0;
        count += scanTomorrowReminders();
        count += scanUpcomingTodayReminders();
        return count;
    }

    private int scanTomorrowReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Appointment> list = appointmentRepository.findAppointmentsForReminders(tomorrow);
        int generated = 0;

        for (Appointment apt : list) {
            if (apt.getPatient() == null || apt.getPatient().getUser() == null) continue;
            Long userId = apt.getPatient().getUser().getId();
            String title = "Appointment Reminder: Tomorrow with Dr. " + apt.getDoctor().getUser().getName();

            // Check if reminder was already sent in the past 24 hours
            if (!notificationService.hasRecentNotification(userId, title, LocalDateTime.now().minusHours(24))) {
                String msg = String.format("You have an appointment scheduled tomorrow (%s) at %s with Dr. %s (%s). Please remember your digital pass.",
                        apt.getAppointmentDate(), apt.getAppointmentTime(),
                        apt.getDoctor().getUser().getName(), apt.getDoctor().getSpecialization());

                notificationService.createNotification(
                        apt.getPatient().getUser(),
                        title,
                        msg,
                        "REMINDER",
                        apt.getId()
                );
                generated++;
            }
        }
        return generated;
    }

    private int scanUpcomingTodayReminders() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Appointment> list = appointmentRepository.findAppointmentsForReminders(today);
        int generated = 0;

        for (Appointment apt : list) {
            if (apt.getPatient() == null || apt.getPatient().getUser() == null) continue;
            Long userId = apt.getPatient().getUser().getId();

            long minutesUntil = ChronoUnit.MINUTES.between(now, apt.getAppointmentTime());
            // If appointment is within 90 minutes and not in the past
            if (minutesUntil > 0 && minutesUntil <= 90) {
                String title = "Upcoming Appointment in ~" + minutesUntil + " min: Dr. " + apt.getDoctor().getUser().getName();

                if (!notificationService.hasRecentNotification(userId, title, LocalDateTime.now().minusHours(2))) {
                    String msg = String.format("Your consultation with Dr. %s is coming up at %s today. Please arrive at Consultation Room %s 10-15 minutes prior.",
                            apt.getDoctor().getUser().getName(), apt.getAppointmentTime(),
                            apt.getDoctor().getConsultationRoom() != null ? apt.getDoctor().getConsultationRoom() : "Main Clinic");

                    notificationService.createNotification(
                            apt.getPatient().getUser(),
                            title,
                            msg,
                            "URGENT_REMINDER",
                            apt.getId()
                    );
                    generated++;
                }
            }
        }
        return generated;
    }
}
