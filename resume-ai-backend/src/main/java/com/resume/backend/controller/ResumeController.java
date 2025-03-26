package com.resume.backend.controller;

import com.resume.backend.ResumeRequest;
import com.resume.backend.model.User;
import com.resume.backend.service.ResumeService;
import com.resume.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/resume")
public class ResumeController {
    @Autowired
    private  ResumeService resumeService;
    @Autowired
    private  UserService userService;
    @Autowired
    private  JdbcTemplate jdbcTemplate;
    

    
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> getResumeData(
            @RequestBody ResumeRequest resumeRequest
    ) throws IOException {
        try {
        
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
            Map<String, Object> resumeData = resumeService.generateResumeResponse(resumeRequest.userDescription());
            boolean hasTelegramConnected = false;
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
                String userId = authentication.getName();
                hasTelegramConnected = resumeService.checkTelegramConnection(userId);
            }
            resumeData.put("telegramConnected", hasTelegramConnected);
            return new ResponseEntity<>(resumeData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint to send a PDF generated on the frontend to Telegram
     */
    @PostMapping("/send-to-telegram")
    public ResponseEntity<?> sendPdfToTelegram(@RequestBody Map<String, Object> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required to send to Telegram"));
            }
            String userId = authentication.getName();
            User user = userService.getUserByOauthId(userId);
            if (user == null && userId.contains("@")) {
                user = userService.getUserByEmail(userId);
            }
            if (user == null) {
                try {
                    user = jdbcTemplate.queryForObject(
                        "SELECT * FROM users WHERE oauth_id = ? OR email = ?", 
                        userService.getUserRowMapper(),
                        userId, userId
                    );
                } catch (Exception e) {
                }
            }
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
            }
            String oauthId = user.getOauthId();

            String base64Pdf = (String) request.get("pdfData");
            if (base64Pdf == null || base64Pdf.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "PDF data is required"));
            }

            if (base64Pdf.contains(",")) {
                base64Pdf = base64Pdf.split(",")[1];
            }

            byte[] pdfBytes = java.util.Base64.getDecoder().decode(base64Pdf);

            String fileName = (String) request.getOrDefault("fileName", "Resume_" + System.currentTimeMillis() + ".pdf");

            boolean sent = resumeService.sendResumeTelegram(oauthId, pdfBytes, fileName);
            
            return ResponseEntity.ok(Map.of(
                "success", sent,
                "message", sent ? "Resume sent to Telegram" : "Failed to send resume to Telegram"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send PDF to Telegram: " + e.getMessage()));
        }
    }
}