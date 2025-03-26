package com.resume.backend.service;

import java.io.IOException;
import java.util.Map;

public interface ResumeService {
    Map<String, Object> generateResumeResponse(String userResumeDescription) throws IOException;
    boolean sendResumeTelegram(String userId, byte[] pdfData, String fileName);
    boolean checkTelegramConnection(String userId);
}