// OAuth2LoginSuccessHandler.java
package com.resume.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.resume.backend.model.User;
import com.resume.backend.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    
    @Autowired
    private UserService userService;
    
    public OAuth2LoginSuccessHandler() {
        setDefaultTargetUrl("/dashboard");
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2User principal = token.getPrincipal();
            String registrationId = token.getAuthorizedClientRegistrationId();
            User user = userService.processOAuthUser(principal, registrationId);
            
            // Always redirect to the Railway production URL
            String redirectUrl = "https://caring-beauty-production-ce6a.up.railway.app/dashboard";
            response.sendRedirect(redirectUrl);
            return;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}