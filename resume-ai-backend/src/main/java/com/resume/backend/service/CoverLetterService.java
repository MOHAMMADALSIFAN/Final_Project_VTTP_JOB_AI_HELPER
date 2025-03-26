package com.resume.backend.service;

import java.io.IOException;
import java.util.Map;

public interface CoverLetterService {
  Map<String, Object> generateCoverLetterResponse(String userCoverLetterDescription) throws IOException;
}