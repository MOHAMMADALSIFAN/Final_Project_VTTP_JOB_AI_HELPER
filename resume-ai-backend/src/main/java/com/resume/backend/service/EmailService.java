package com.resume.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendWelcomeEmail(String toEmail, String name) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Welcome to Job AI Helper!");
        mailMessage.setText("Dear " + name + ",\n\n" +
                "Welcome to Job AI Helper! We're excited to have you onboard.\n\n" +
                "Our AI-powered platform helps you streamline your job search with the following features:\n" +
                "✅ Job Search using roles and locations\n" +
                "✅ AI Resume Builder for professional resumes (Download in PDF or send to Telegram)\n" +
                "✅ AI Cover Letter Builder for personalized cover letters (Download in PDF or send to Telegram)\n" +
                "✅ AI Email Reply Builder to craft effective job application replies\n\n" +
                "Simply provide your details, and our AI will generate tailored documents to enhance your job search.\n\n" +
                "If you have any questions, feel free to reach out—we’re here to help!\n\n" +
                "Best regards,\n" +
                "The AI Job Search Assistant Team");
        mailMessage.setFrom(fromEmail);
        
        javaMailSender.send(mailMessage);
    }
  }