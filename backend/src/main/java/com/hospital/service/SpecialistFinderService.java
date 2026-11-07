package com.hospital.service;

import com.hospital.dto.DoctorDto;
import com.hospital.dto.SpecialistRecommendationResponse;
import com.hospital.entity.Doctor;
import com.hospital.entity.SpecializationKeyword;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.SpecializationKeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpecialistFinderService {

    private final SpecializationKeywordRepository keywordRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;

    private static final List<String> EMERGENCY_TRIGGERS = Arrays.asList(
            "chest pain", "heart attack", "can't breathe", "cannot breathe", "difficulty breathing",
            "shortness of breath", "severe bleeding", "unconscious", "stroke", "seizure",
            "coughing blood", "heavy bleeding", "poison", "suicide", "collapse"
    );

    public SpecialistFinderService(SpecializationKeywordRepository keywordRepository,
                                   DoctorRepository doctorRepository,
                                   DoctorService doctorService) {
        this.keywordRepository = keywordRepository;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
    }

    public SpecialistRecommendationResponse findSpecialist(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Symptom query cannot be empty");
        }

        String lowerQuery = query.toLowerCase();
        boolean isEmergency = checkEmergency(lowerQuery);

        List<SpecializationKeyword> allKeywords = keywordRepository.findAll();
        Map<String, MatchedResult> scores = new HashMap<>();

        for (SpecializationKeyword sk : allKeywords) {
            String spec = sk.getSpecialization();
            String[] words = sk.getKeywords().toLowerCase().split(",");
            int matchCount = 0;
            List<String> matchedWords = new ArrayList<>();

            for (String w : words) {
                String trimmed = w.trim();
                if (!trimmed.isEmpty() && lowerQuery.contains(trimmed)) {
                    matchCount++;
                    matchedWords.add(trimmed);
                }
            }

            if (matchCount > 0) {
                scores.put(spec, new MatchedResult(sk, matchCount, matchedWords));
            }
        }

        List<SpecialistRecommendationResponse.RecommendationItem> recList = new ArrayList<>();

        // If emergency detected, make sure Emergency Medicine or Cardiology is on top
        if (isEmergency) {
            SpecialistRecommendationResponse.RecommendationItem emergencyItem = new SpecialistRecommendationResponse.RecommendationItem();
            emergencyItem.setSpecialization("Emergency Medicine");
            emergencyItem.setDepartment("Emergency Department");
            emergencyItem.setConfidence("CRITICAL");
            emergencyItem.setExplanation("Your reported symptoms match red-flag emergency symptoms requiring immediate attention.");
            emergencyItem.setMatchedKeywords(Arrays.asList("emergency-level symptoms"));

            // Check if we have emergency or cardiology doctors
            List<Doctor> cardioDocs = doctorRepository.findBySpecializationIgnoreCase("Cardiology");
            List<DoctorDto> cardioDtos = cardioDocs.stream().map(d -> doctorService.getDoctorById(d.getId())).collect(Collectors.toList());
            emergencyItem.setDoctors(cardioDtos);
            recList.add(emergencyItem);
        }

        // Sort matched specializations by match score descending
        List<Map.Entry<String, MatchedResult>> sortedMatches = scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue().matchCount, a.getValue().matchCount))
                .collect(Collectors.toList());

        for (Map.Entry<String, MatchedResult> entry : sortedMatches) {
            SpecializationKeyword sk = entry.getValue().keyword;
            int count = entry.getValue().matchCount;
            List<String> matched = entry.getValue().matchedWords;

            SpecialistRecommendationResponse.RecommendationItem item = new SpecialistRecommendationResponse.RecommendationItem();
            item.setSpecialization(sk.getSpecialization());
            item.setDepartment(sk.getDepartment() != null ? sk.getDepartment() : sk.getSpecialization());
            item.setExplanation(sk.getDescription() != null ? sk.getDescription()
                    : "Matches reported symptoms related to " + String.join(", ", matched));
            item.setConfidence(count >= 3 ? "HIGH" : (count == 2 ? "MEDIUM" : "LOW"));
            item.setMatchedKeywords(matched);

            List<Doctor> docs = doctorRepository.findBySpecializationIgnoreCase(sk.getSpecialization());
            List<DoctorDto> dtos = docs.stream().map(d -> doctorService.getDoctorById(d.getId())).collect(Collectors.toList());
            item.setDoctors(dtos);

            recList.add(item);
        }

        // If no match found and not emergency, fallback to General Medicine
        if (recList.isEmpty()) {
            SpecialistRecommendationResponse.RecommendationItem general = new SpecialistRecommendationResponse.RecommendationItem();
            general.setSpecialization("General Medicine");
            general.setDepartment("Internal Medicine");
            general.setConfidence("GENERAL");
            general.setExplanation("No specific specialist match detected. We recommend starting with a General Physician for comprehensive evaluation.");
            general.setMatchedKeywords(Collections.emptyList());

            List<Doctor> docs = doctorRepository.findBySpecializationIgnoreCase("General Medicine");
            if (docs.isEmpty()) {
                docs = doctorRepository.findAll().stream().limit(3).collect(Collectors.toList());
            }
            List<DoctorDto> dtos = docs.stream().map(d -> doctorService.getDoctorById(d.getId())).collect(Collectors.toList());
            general.setDoctors(dtos);

            recList.add(general);
        }

        String emergencyAlert = null;
        if (isEmergency) {
            emergencyAlert = "EMERGENCY NOTICE: Symptoms entered indicate potential urgent health danger. Please dial emergency services (911/112/108) or go to the nearest emergency room immediately.";
        }

        return new SpecialistRecommendationResponse(
                recList,
                "Medical Disclaimer: This triage tool is designed for informational guidance only and does not constitute clinical diagnosis or medical treatment. Always consult a qualified medical professional.",
                isEmergency,
                emergencyAlert
        );
    }

    private boolean checkEmergency(String lower) {
        for (String trigger : EMERGENCY_TRIGGERS) {
            if (lower.contains(trigger)) {
                return true;
            }
        }
        return false;
    }

    // Admin methods for keyword management
    public List<SpecializationKeyword> getAllKeywords() {
        return keywordRepository.findAllByOrderBySpecializationAsc();
    }

    @Transactional
    public SpecializationKeyword saveKeyword(SpecializationKeyword keyword) {
        return keywordRepository.save(keyword);
    }

    @Transactional
    public void deleteKeyword(Long id) {
        keywordRepository.deleteById(id);
    }

    private static class MatchedResult {
        SpecializationKeyword keyword;
        int matchCount;
        List<String> matchedWords;

        MatchedResult(SpecializationKeyword keyword, int matchCount, List<String> matchedWords) {
            this.keyword = keyword;
            this.matchCount = matchCount;
            this.matchedWords = matchedWords;
        }
    }
}
