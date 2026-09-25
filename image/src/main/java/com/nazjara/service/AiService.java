package com.nazjara.service;

import com.nazjara.model.Question;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Image capabilities: generating an image from text (OpenAI) and describing an
 * uploaded image (Claude).
 */
public interface AiService {
	/**
	 * Generates an image from a text description.
	 *
	 * @param question the image description
	 * @return PNG image bytes
	 */
	byte[] getImage(Question question);
	/**
	 * Describes the contents of an uploaded image.
	 *
	 * @param file the uploaded image (JPEG, PNG, GIF or WebP)
	 * @return the model's description
	 * @throws IOException if the upload cannot be read
	 */
	String getDescription(MultipartFile file) throws IOException;
}
