package com.resume.backend.controller;

import com.resume.backend.model.EmailHistory;
import com.resume.backend.model.EmailRequest;
import com.resume.backend.service.EmailGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    @Autowired
    private EmailGeneratorService emailGeneratorService;
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest) {
        String response = emailGeneratorService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<EmailHistory>> getEmailHistory() {
        List<EmailHistory> history = emailGeneratorService.getAllEmailHistory();
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/history/{id}")
    public ResponseEntity<?> getEmailHistoryById(@PathVariable String id) {
        EmailHistory emailHistory = emailGeneratorService.getEmailHistoryById(id);
        if (emailHistory == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Email history not found with ID: " + id));
        }
        return ResponseEntity.ok(emailHistory);
    }
    
    @GetMapping("/history/search")
    public ResponseEntity<List<EmailHistory>> searchEmailHistory(@RequestParam String term) {
        List<EmailHistory> results = emailGeneratorService.searchEmailHistory(term);
        return ResponseEntity.ok(results);
    }
    
    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteEmailHistory(@PathVariable String id) {
        try {
            emailGeneratorService.deleteEmailHistory(id);
            return ResponseEntity.ok(Map.of("message", "Email history deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete email history: " + e.getMessage()));
        }
    }
}