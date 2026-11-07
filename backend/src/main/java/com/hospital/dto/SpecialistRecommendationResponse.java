package com.hospital.dto;

import java.util.ArrayList;
import java.util.List;

public class SpecialistRecommendationResponse {
    private String symptomsQuery;
    private String disclaimer = "Medical Disclaimer: This triage tool is designed for informational guidance only and does not constitute clinical diagnosis or medical treatment. Always consult a qualified medical professional.";
    private boolean emergency;
    private String emergencyAlert;
    private List<RecommendationItem> recommendations = new ArrayList<>();

    public SpecialistRecommendationResponse() {
    }

    public SpecialistRecommendationResponse(List<RecommendationItem> recommendations, String disclaimer, boolean emergency, String emergencyAlert) {
        this.recommendations = recommendations;
        this.disclaimer = disclaimer;
        this.emergency = emergency;
        this.emergencyAlert = emergencyAlert;
    }

    public static class RecommendationItem {
        private String specialization;
        private String department;
        private String explanation;
        private String confidence; // CRITICAL, HIGH, MEDIUM, LOW, GENERAL
        private List<String> matchedKeywords = new ArrayList<>();
        private List<DoctorDto> doctors = new ArrayList<>();

        public RecommendationItem() {
        }

        public RecommendationItem(String specialization, String department, String explanation,
                                  String confidence, List<String> matchedKeywords, List<DoctorDto> doctors) {
            this.specialization = specialization;
            this.department = department;
            this.explanation = explanation;
            this.confidence = confidence;
            this.matchedKeywords = matchedKeywords;
            this.doctors = doctors;
        }

        public String getSpecialization() {
            return specialization;
        }

        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public String getConfidence() {
            return confidence;
        }

        public void setConfidence(String confidence) {
            this.confidence = confidence;
        }

        public List<String> getMatchedKeywords() {
            return matchedKeywords;
        }

        public void setMatchedKeywords(List<String> matchedKeywords) {
            this.matchedKeywords = matchedKeywords;
        }

        public List<DoctorDto> getDoctors() {
            return doctors;
        }

        public void setDoctors(List<DoctorDto> doctors) {
            this.doctors = doctors;
        }
    }

    public String getSymptomsQuery() {
        return symptomsQuery;
    }

    public void setSymptomsQuery(String symptomsQuery) {
        this.symptomsQuery = symptomsQuery;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public String getEmergencyAlert() {
        return emergencyAlert;
    }

    public void setEmergencyAlert(String emergencyAlert) {
        this.emergencyAlert = emergencyAlert;
    }

    public List<RecommendationItem> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<RecommendationItem> recommendations) {
        this.recommendations = recommendations;
    }
}
