package com.resume.backend.model;

public class EmailResponse {
    private String generatedEmail;
    private String historyId;
    
    public EmailResponse() {
    }
    
    public EmailResponse(String generatedEmail, String historyId) {
        this.generatedEmail = generatedEmail;
        this.historyId = historyId;
    }
    
    public String getGeneratedEmail() {
        return generatedEmail;
    }
    
    public void setGeneratedEmail(String generatedEmail) {
        this.generatedEmail = generatedEmail;
    }
    
    public String getHistoryId() {
        return historyId;
    }
    
    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }
}