package com.resume.backend.model.OpenRouterHelper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChatRequest {
  private String model;
        private List<Message> messages;
        
        
        @JsonProperty("temperature")
        private Double temperature;
        
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        
        public String getModel() {
            return model;
        }
        
        public void setModel(String model) {
            this.model = model;
        }
        
        public List<Message> getMessages() {
            return messages;
        }
        
        public void setMessages(List<Message> messages) {
            this.messages = messages;
        }
        
        public Double getTemperature() {
            return temperature;
        }
        
        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
        
        public Integer getMaxTokens() {
            return maxTokens;
        }
        
        public void setMaxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
        }
    }
    
