package com.nazjara.service;

import com.nazjara.model.Question;
import org.springframework.web.multipart.MultipartFile;

public interface OpenAIService {
	byte[] getImage(Question question);
	String getDescription(MultipartFile file);
}
