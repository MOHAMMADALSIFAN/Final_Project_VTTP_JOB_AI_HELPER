package com.resume.backend;

public record ResumeRequest(
    String userDescription,
    Boolean sendToTelegram
) {}