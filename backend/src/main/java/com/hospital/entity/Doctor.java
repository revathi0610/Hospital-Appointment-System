package com.hospital.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Doctor Entity containing medical specialty and professional credentials.
 * Links 1-to-1 with a User record where role='DOCTOR'.
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 100)
    private String specialization;

    @Column(nullable = false, length = 100)
    private String qualification;

    @Column(nullable = false)
    private Integer experience = 0; // In years

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee = BigDecimal.valueOf(50.00);

    @Column(name = "availability_status", nullable = false, length = 20)
    private String availabilityStatus = "AVAILABLE"; // AVAILABLE, UNAVAILABLE, ON_LEAVE

    @Column(name = "avg_consultation_duration", nullable = false)
    private Integer avgConsultationDuration = 15; // in minutes (Default 15 min)

    @Column(length = 100)
    private String department = "Outpatient Department";

    @Column(name = "consultation_room", length = 50)
    private String consultationRoom = "Cabin 101";

    public Doctor() {
    }

    public Doctor(User user, String specialization, String qualification, Integer experience, BigDecimal consultationFee, String availabilityStatus) {
        this.user = user;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.availabilityStatus = (availabilityStatus != null) ? availabilityStatus : "AVAILABLE";
        this.avgConsultationDuration = 15;
    }

    public Doctor(User user, String specialization, String qualification, Integer experience, BigDecimal consultationFee, String availabilityStatus, Integer avgConsultationDuration) {
        this.user = user;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.availabilityStatus = (availabilityStatus != null) ? availabilityStatus : "AVAILABLE";
        this.avgConsultationDuration = (avgConsultationDuration != null) ? avgConsultationDuration : 15;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getAvgConsultationDuration() {
        return avgConsultationDuration != null ? avgConsultationDuration : 15;
    }

    public void setAvgConsultationDuration(Integer avgConsultationDuration) {
        this.avgConsultationDuration = avgConsultationDuration;
    }

    public String getDepartment() {
        return department != null ? department : "Outpatient Department";
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getConsultationRoom() {
        return consultationRoom != null ? consultationRoom : "Cabin 101";
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }
}
