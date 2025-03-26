package com.resume.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.model.OpenRouterHelper.ChatRequest;
import com.resume.backend.model.OpenRouterHelper.ChatResponse;
import com.resume.backend.model.OpenRouterHelper.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@Service
public class OpenRouterService {

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";

    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    public String sendRequestToOpenRouter(String userMessage) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openRouterApiKey);
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setModel("meta-llama/llama-3.3-70b-instruct:free");
            Message systemMessage = new Message();
            systemMessage.setRole("system");
            systemMessage.setContent("You are a resume builder assistant. You MUST format your responses with a <think></think> section followed by a JSON object in ```json``` code blocks. Always maintain this exact format.");
            Message userMsg = new Message();
            userMsg.setRole("user");
            userMsg.setContent(userMessage);
            List<Message> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMsg);
            chatRequest.setMessages(messages);
            
            // Add parameters for better generation
            chatRequest.setTemperature(0.1); // Lower temperature for more consistent output
            chatRequest.setMaxTokens(4000); // Ensure enough tokens for complete response
            
            // Convert to JSON
            String requestBody = objectMapper.writeValueAsString(chatRequest);
            
            // Create the HTTP entity
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            // Send the POST request
            ResponseEntity<String> response = restTemplate.exchange(
                OPENROUTER_API_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            // Log full response for debugging
            // System.out.println("Raw API Response: " + response.getBody());

            // Parse the response
            ChatResponse chatResponse = objectMapper.readValue(response.getBody(), ChatResponse.class);
            if (chatResponse != null && chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
                return chatResponse.getChoices().get(0).getMessage().getContent();
            } else {
                return "No content received from API";
            }
            
        } catch (RestClientException e) {
            System.err.println("REST client error: " + e.getMessage());
            return "Error communicating with OpenRouter API: " + e.getMessage();
        } catch (JsonProcessingException e) {
            System.err.println("JSON processing error: " + e.getMessage());
            e.printStackTrace();
            return "Error processing JSON: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return "Unexpected error: " + e.getMessage();
        }
    }
    

}