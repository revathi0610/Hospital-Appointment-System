package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.ChatRequest;
import com.hospital.dto.ChatResponse;
import com.hospital.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatResponse>> handleMessage(@RequestBody ChatRequest request) {
        ChatResponse response = chatbotService.processChat(request);
        return ResponseEntity.ok(ApiResponse.success("Message processed", response));
    }
}
