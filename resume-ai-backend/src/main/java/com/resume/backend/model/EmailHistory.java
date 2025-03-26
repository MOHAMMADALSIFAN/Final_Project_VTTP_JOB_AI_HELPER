package com.resume.backend.model;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;


@Document(collection = "email_history")
public class EmailHistory {
    
    @Id
    private String id;
    private String originalContent;
    private String generatedReply;
    private String tone;
    private LocalDateTime createdAt;
    
    public EmailHistory() {
        this.createdAt = LocalDateTime.now();
    }
    
    public EmailHistory(String originalContent, String generatedReply, String tone) {
        this.originalContent = originalContent;
        this.generatedReply = generatedReply;
        this.tone = tone;
        this.createdAt = LocalDateTime.now();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOriginalContent() {
        return originalContent;
    }
    
    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }
    
    public String getGeneratedReply() {
        return generatedReply;
    }
    
    public void setGeneratedReply(String generatedReply) {
        this.generatedReply = generatedReply;
    }
    
    public String getTone() {
        return tone;
    }
    
    public void setTone(String tone) {
        this.tone = tone;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "EmailHistory{" +
                "id='" + id + '\'' +
                ", tone='" + tone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}