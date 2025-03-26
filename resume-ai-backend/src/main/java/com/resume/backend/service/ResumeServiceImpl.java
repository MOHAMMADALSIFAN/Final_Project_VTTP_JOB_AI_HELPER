package com.resume.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private  OpenRouterService openRouterService;
    @Autowired
    private  JdbcTemplate jdbcTemplate;
    @Autowired
    private TelegramBotService telegramBotService;

    @Override
    public Map<String, Object> generateResumeResponse(String userResumeDescription) throws IOException {
        String promptString = this.loadPromptFromFile("resume_prompt.txt");
        String promptContent = this.putValuesToTemplate(promptString, Map.of(
                "userDescription", userResumeDescription
        ));

        String response = openRouterService.sendRequestToOpenRouter(promptContent);

        Map<String, Object> stringObjectMap = parseMultipleResponses(response);
        return stringObjectMap;
    }

    @Override
    public boolean checkTelegramConnection(String userId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM telegram_chats WHERE user_id = ?",
                    Integer.class,
                    userId
            );
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean sendResumeTelegram(String userId, byte[] pdfData, String fileName) {
        try {
            System.out.println("Attempting to send PDF to user: " + userId);
            if (telegramBotService == null) {
                System.out.println("Telegram service is not available");
                return false;
            }
            boolean isConnected = checkTelegramConnection(userId);
            System.out.println("User Telegram connection status: " + isConnected);
            
            if (!isConnected) {
                System.out.println("User has not connected Telegram yet");
                return false;
            }
          
            savePdfToDatabase(userId, pdfData, fileName);
            System.out.println("PDF saved to database");
        
            boolean sent = telegramBotService.sendResumePdf(userId, pdfData, fileName);
            System.out.println("PDF send result: " + sent);
            return sent;
        } catch (Exception e) {
            System.err.println("Exception sending PDF to Telegram: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
 
    private void savePdfToDatabase(String userId, byte[] pdfData, String fileName) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO resume_pdfs (user_id, pdf_data, created_at) VALUES (?, ?, NOW())",
                    userId, pdfData
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  String loadPromptFromFile(String filename) throws IOException {
    ClassPathResource resource = new ClassPathResource(filename);
    try (InputStream inputStream = resource.getInputStream()) {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}

    String putValuesToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return template;
    }

    public static Map<String, Object> parseMultipleResponses(String response) {
        Map<String, Object> jsonResponse = new HashMap<>();
        int thinkStart = response.indexOf("<think>") + 7;
        int thinkEnd = response.indexOf("</think>");
        if (thinkStart != -1 && thinkEnd != -1) {
            String thinkContent = response.substring(thinkStart, thinkEnd).trim();
            jsonResponse.put("think", thinkContent);
        } else {
            jsonResponse.put("think", null); 
        }
        int jsonStart = response.indexOf("```json") + 7; // Start after ```json
        int jsonEnd = response.lastIndexOf("```");       // End before ```
        if (jsonStart != -1 && jsonEnd != -1 && jsonStart < jsonEnd) {
            String jsonContent = response.substring(jsonStart, jsonEnd).trim();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> dataContent = objectMapper.readValue(jsonContent, Map.class);
                jsonResponse.put("data", dataContent);
            } catch (Exception e) {
                jsonResponse.put("data", null); // Handle invalid JSON
                System.err.println("Invalid JSON format in the response: " + e.getMessage());
            }
        } else {
            jsonResponse.put("data", null); // Handle missing JSON
        }

        return jsonResponse;
    }
}
