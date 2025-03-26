package com.resume.backend.controller;


import com.resume.backend.service.CoverLetterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/coverletter")
public class CoverLetterController {

  @Autowired
  private CoverLetterService coverLetterService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateCoverLetter(@RequestBody Map<String, String> request) {
        try {
            String userDescription = request.get("userDescription");
            Map<String, Object> response = coverLetterService.generateCoverLetterResponse(userDescription);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}