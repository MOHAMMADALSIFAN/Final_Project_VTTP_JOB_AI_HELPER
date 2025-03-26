package com.resume.backend.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.resume.backend.model.User;
import com.resume.backend.service.ResumeService;
import com.resume.backend.service.TelegramBotService;
import com.resume.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/telegram")
@CrossOrigin(origins = "*")
public class TelegramController {

    @Autowired
    private  TelegramBotService telegramBotService;
    @Autowired
    private UserService userService;
    @Autowired
    private  ResumeService resumeService;
    @Autowired
    private  JdbcTemplate jdbcTemplate;
    @Autowired
    private  ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    


    /**
     * Generate a QR code for linking Telegram account
     */
    @GetMapping("/qr-code")
    public ResponseEntity<String> generateQrCode() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
                String temporaryId = "temp_" + UUID.randomUUID().toString();
                String token = telegramBotService.generateTempLinkToken(temporaryId);
                String deepLink = "https://t.me/" + botUsername + "?start=" + token;
                String qrCodeBase64 = generateQRCodeImage(deepLink, 250, 250);
                Map<String, Object> response = new HashMap<>();
                response.put("qrCode", qrCodeBase64);
                response.put("deepLink", deepLink);
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(response));
            }
            String userEmail = authentication.getName();
            User user = null;
            user = userService.getUserByOauthId(userEmail);
            if (user == null) {
                user = userService.getUserByEmail(userEmail);
            }
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not found\"}");
            }
            String oauthId = user.getOauthId();
            if (oauthId == null || oauthId.isEmpty()) {
                oauthId = "local_" + UUID.randomUUID().toString();
                jdbcTemplate.update(
                    "UPDATE users SET oauth_id = ? WHERE id = ?",
                    oauthId, user.getId()
                );
                user.setOauthId(oauthId);
            }

            String token = telegramBotService.generateLinkToken(oauthId);
            String deepLink = "https://t.me/" + botUsername + "?start=" + token;
            String qrCodeBase64 = generateQRCodeImage(deepLink, 250, 250);
            Map<String, Object> response = new HashMap<>();
            response.put("qrCode", qrCodeBase64);
            response.put("deepLink", deepLink);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to generate QR code: " + e.getMessage());
            
            try {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.writeValueAsString(error));
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal server error\"}");
            }
        }
    }
    
    /**
     * Generate QR code image as Base64 string
     */
    private String generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    @GetMapping("/check-connection")
    public ResponseEntity<Map<String, Object>> checkConnection() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
                return ResponseEntity.ok(Map.of("connected", false));
            }
            String userId = authentication.getName();
            if (userId.contains("@")) {
                User user = userService.getUserByEmail(userId);
                if (user != null) {
                    userId = user.getOauthId();
                }
            }
            boolean connected = telegramBotService.hasTelegramConnection(userId);
            
            return ResponseEntity.ok(Map.of("connected", connected));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("connected", false));
        }
    }
}