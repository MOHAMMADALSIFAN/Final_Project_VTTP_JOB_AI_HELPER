package com.resume.backend.service;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.resume.backend.model.User;

public interface UserService {
  // OAuth2 related methods
  String extractUserId(OAuth2User principal);
  String getCurrentUserName(String oauthId);
  User processOAuthUser(OAuth2User principal, String registrationId);
  User getUserByOauthId(String oauthId);
  
  // Session authentication related methods
  User validateCredentials(String email, String password);
  boolean existsByEmail(String email);
  User createUser(String name, String email, String password);
  void updateLastLogin(Long userId);
  
  // For Spring Security
  UserDetails loadUserByUsername(String username);
  
  // Utility method
  RowMapper<User> getUserRowMapper();

  User getUserByEmail(String email);
}