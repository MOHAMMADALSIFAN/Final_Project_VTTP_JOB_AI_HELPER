package com.resume.backend.model;

import java.time.LocalDateTime;



public class User {
  private Long id;
  private String oauthId;
  private String name;
  private String email;
  private String picture;
  private String provider;
  private LocalDateTime createdAt;
  private LocalDateTime lastLogin;
  private String password; 
  public User() {
  }
  public User(Long id, String oauthId, String name, String email, String picture, String provider,
  LocalDateTime createdAt, LocalDateTime lastLogin, String password) {
this.id = id;
this.oauthId = oauthId;
this.name = name;
this.email = email;
this.picture = picture;
this.provider = provider;
this.createdAt = createdAt;
this.lastLogin = lastLogin;
this.password = password;
}
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getOauthId() {
    return oauthId;
  }
  public void setOauthId(String oauthId) {
    this.oauthId = oauthId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getPicture() {
    return picture;
  }
  public void setPicture(String picture) {
    this.picture = picture;
  }
  public String getProvider() {
    return provider;
  }
  public void setProvider(String provider) {
    this.provider = provider;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
  public LocalDateTime getLastLogin() {
    return lastLogin;
  }
  public void setLastLogin(LocalDateTime lastLogin) {
    this.lastLogin = lastLogin;
  }
  public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}

 


}