package com.resume.backend.service;

import com.resume.backend.model.EmailHistory;
import com.resume.backend.model.EmailRequest;
import com.resume.backend.repository.EmailHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailGeneratorService {

    @Autowired
    private  OpenRouterService openRouterService;
    @Autowired
    private  EmailHistoryRepository emailHistoryRepository;
    

    public String generateEmailReply(EmailRequest emailRequest) {
        try {

            String prompt = buildPrompt(emailRequest);
            String generatedReply = openRouterService.sendRequestToOpenRouter(prompt);
            
            // Save to MongoDB
            EmailHistory emailHistory = new EmailHistory(
                emailRequest.getEmailContent(),
                generatedReply,
                emailRequest.getTone()
            );
            emailHistoryRepository.save(emailHistory);
            
            return generatedReply;
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating email: " + e.getMessage();
        }
    }
    
    /**
     * Get all email history
     */
    public List<EmailHistory> getAllEmailHistory() {
        return emailHistoryRepository.findAll();
    }
    
    /**
     * Get email history by ID
     */
    public EmailHistory getEmailHistoryById(String id) {
        return emailHistoryRepository.findById(id);
    }
    
    /**
     * Search email history
     */
    public List<EmailHistory> searchEmailHistory(String searchTerm) {
        return emailHistoryRepository.searchInContent(searchTerm);
    }
    
    /**
     * Delete email history
     */
    public void deleteEmailHistory(String id) {
        emailHistoryRepository.deleteById(id);
    }
    
    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line. ");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        prompt.append("\n\nPlease provide just the email reply text without any additional formatting, think sections, or JSON objects.");
        return prompt.toString();
    }
}