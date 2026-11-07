package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.SpecialistRecommendationResponse;
import com.hospital.entity.SpecializationKeyword;
import com.hospital.service.SpecialistFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")
public class SpecialistController {

    private final SpecialistFinderService specialistFinderService;

    public SpecialistController(SpecialistFinderService specialistFinderService) {
        this.specialistFinderService = specialistFinderService;
    }

    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<SpecialistRecommendationResponse>> recommendSpecialist(@RequestParam String query) {
        SpecialistRecommendationResponse rec = specialistFinderService.findSpecialist(query);
        return ResponseEntity.ok(ApiResponse.success("Specialist recommendations generated", rec));
    }

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<SpecializationKeyword>>> getAllKeywords() {
        List<SpecializationKeyword> list = specialistFinderService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.success("Keywords retrieved", list));
    }

    @PostMapping("/keywords")
    public ResponseEntity<ApiResponse<SpecializationKeyword>> saveKeyword(@RequestBody SpecializationKeyword keyword) {
        SpecializationKeyword saved = specialistFinderService.saveKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success("Keyword saved successfully", saved));
    }

    @DeleteMapping("/keywords/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKeyword(@PathVariable Long id) {
        specialistFinderService.deleteKeyword(id);
        return ResponseEntity.ok(ApiResponse.success("Keyword deleted successfully", null));
    }
}
