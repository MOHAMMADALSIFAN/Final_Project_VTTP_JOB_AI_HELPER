package com.resume.backend.model.OpenRouterHelper;

public class Message {
  private String role;
  private String content;
  private String refusal;
  
  public String getRole() {
      return role;
  }
  
  public void setRole(String role) {
      this.role = role;
  }
  
  public String getContent() {
      return content;
  }
  
  public void setContent(String content) {
      this.content = content;
  }
  
  public String getRefusal() {
      return refusal;
  }
  
  public void setRefusal(String refusal) {
      this.refusal = refusal;
  }
}
