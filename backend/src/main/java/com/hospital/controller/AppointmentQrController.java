package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.DigitalCardDto;
import com.hospital.dto.QrVerifyRequest;
import com.hospital.dto.QrVerifyResponse;
import com.hospital.service.QrCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentQrController {

    private final QrCodeService qrCodeService;

    public AppointmentQrController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    // Get complete digital appointment card info (for modal & printable pass)
    @GetMapping("/{id}/card")
    public ResponseEntity<ApiResponse<DigitalCardDto>> getDigitalAppointmentCard(@PathVariable Long id) {
        DigitalCardDto card = qrCodeService.getDigitalAppointmentCard(id);
        return ResponseEntity.ok(ApiResponse.success("Digital pass retrieved", card));
    }

    // Verify appointment pass via POST token
    @PostMapping("/verify-qr")
    public ResponseEntity<ApiResponse<QrVerifyResponse>> verifyQrPass(@RequestBody QrVerifyRequest request) {
        QrVerifyResponse resp = qrCodeService.verifyAppointmentToken(request.getToken());
        return ResponseEntity.ok(ApiResponse.success("Verification processed", resp));
    }

    // Direct token verification endpoint (for scanned URLs or quick check)
    @GetMapping("/verify-qr/{token}")
    public ResponseEntity<ApiResponse<QrVerifyResponse>> verifyQrTokenDirect(@PathVariable String token) {
        QrVerifyResponse resp = qrCodeService.verifyAppointmentToken(token);
        return ResponseEntity.ok(ApiResponse.success("Verification processed", resp));
    }
}
