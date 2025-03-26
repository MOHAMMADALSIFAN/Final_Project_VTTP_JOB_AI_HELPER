package com.resume.backend.controller;

import com.resume.backend.model.LoginRequest;
import com.resume.backend.model.SignupRequest;
import com.resume.backend.model.User;
import com.resume.backend.service.EmailService;
import com.resume.backend.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }
        
        response.put("authenticated", true);
        
        String userId = userService.extractUserId(principal);
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            String registrationId = token.getAuthorizedClientRegistrationId();
            User user = userService.processOAuthUser(principal, registrationId);
            
            if (user != null) {
                response.put("userId", userId);
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("provider", user.getProvider());
                response.put("picture", user.getPicture());
                return ResponseEntity.ok(response);
            }
        }
        String userName = userService.getCurrentUserName(userId);
        response.put("name", userName != null ? userName : principal.getAttribute("name"));
        response.put("email", principal.getAttribute("email"));
        response.put("picture", principal.getAttribute("picture"));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }
        
        response.put("authenticated", true);
        
        String userId = userService.extractUserId(principal);
        User user = null;
        
        if (userId != null) {
            user = userService.getUserByOauthId(userId);
        }
        
        if (user != null) {
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("picture", user.getPicture());
            response.put("provider", user.getProvider());
            response.put("createdAt", user.getCreatedAt());
            response.put("lastLogin", user.getLastLogin());
        } else {
            response.put("name", principal.getAttribute("name"));
            response.put("email", principal.getAttribute("email"));
            response.put("picture", principal.getAttribute("picture"));
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
          
            User user = findUserByEmail(loginRequest.getEmail());
            
            if (user != null) {
              
                session.setAttribute("userId", user.getId());
                session.setAttribute("userEmail", user.getEmail());
                
               
                userService.updateLastLogin(user.getId());
                
               
                response.put("authenticated", true);
                response.put("userId", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("picture", user.getPicture());
                
                return ResponseEntity.ok(response);
            }
        } catch (BadCredentialsException e) {
            
        }
        
        response.put("authenticated", false);
        response.put("message", "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest signupRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        
        if (userService.existsByEmail(signupRequest.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already in use");
            return ResponseEntity.badRequest().body(response);
        }
        
      
        User user = userService.createUser(
            signupRequest.getName(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())
        );
        
  
        session.setAttribute("userId", user.getId());
        session.setAttribute("userEmail", user.getEmail());
        
       
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        
        response.put("success", true);
        response.put("message", "User registered successfully");
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
  
    private User findUserByEmail(String email) {
        List<User> users = jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?",
            userService.getUserRowMapper(),
            email
        );
        return users.isEmpty() ? null : users.get(0);
    }
}