package com.hospital.entity;

import jakarta.persistence.*;

/**
 * SpecializationKeyword Entity mapping symptoms to medical departments.
 * Supports Feature 2: Symptom -> Specialist Finder (Admin Configurable).
 */
@Entity
@Table(name = "specialization_keywords")
public class SpecializationKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String specialization;

    @Column(length = 100)
    private String department;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String keywords;

    @Column(columnDefinition = "TEXT")
    private String description;

    public SpecializationKeyword() {
    }

    public SpecializationKeyword(String specialization, String keywords, String description) {
        this.specialization = specialization;
        this.department = specialization;
        this.keywords = keywords;
        this.description = description;
    }

    public SpecializationKeyword(String specialization, String department, String keywords, String description) {
        this.specialization = specialization;
        this.department = department;
        this.keywords = keywords;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return (department != null && !department.trim().isEmpty()) ? department : specialization;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
