package com.nazjara.service;

import com.nazjara.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AiService {
	byte[] getImage(Question question);
	String getDescription(MultipartFile file) throws IOException;
}
