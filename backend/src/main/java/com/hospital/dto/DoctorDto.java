package com.hospital.dto;

import java.math.BigDecimal;

public class DoctorDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String specialization;
    private String qualification;
    private Integer experience;
    private BigDecimal consultationFee;
    private String availabilityStatus;
    private String department;
    private String consultationRoom;
    private Integer avgConsultationDuration;

    public DoctorDto() {
    }

    public DoctorDto(Long id, Long userId, String name, String email, String phone,
                     String specialization, String qualification, Integer experience,
                     BigDecimal consultationFee, String availabilityStatus) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.availabilityStatus = availabilityStatus;
        this.avgConsultationDuration = 15;
    }

    public DoctorDto(Long id, Long userId, String name, String email, String phone,
                     String specialization, String qualification, Integer experience,
                     BigDecimal consultationFee, String availabilityStatus,
                     String department, String consultationRoom, Integer avgConsultationDuration) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experience = experience;
        this.consultationFee = consultationFee;
        this.availabilityStatus = availabilityStatus;
        this.department = department;
        this.consultationRoom = consultationRoom;
        this.avgConsultationDuration = avgConsultationDuration != null ? avgConsultationDuration : 15;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getConsultationRoom() {
        return consultationRoom;
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }

    public Integer getAvgConsultationDuration() {
        return avgConsultationDuration;
    }

    public void setAvgConsultationDuration(Integer avgConsultationDuration) {
        this.avgConsultationDuration = avgConsultationDuration;
    }
}
