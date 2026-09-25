package com.nazjara.rest;

import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

/**
 * REST entry points for image generation and image understanding.
 */
@RestController
@RequiredArgsConstructor
public class QuestionController {

	private final AiService aiService;

	/**
	 * Generates an image.
	 *
	 * @param question JSON body {@code {"question": "<image description>"}}
	 * @return the PNG image
	 */
	@PostMapping(value = "/image", produces = IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestBody Question question) {
		return aiService.getImage(question);
	}

	/**
	 * Describes an uploaded image.
	 *
	 * @param file multipart field {@code file}
	 * @return the description
	 * @throws IOException if the upload cannot be read
	 */
	@PostMapping(value = "/vision", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> upload(@RequestParam MultipartFile file) throws IOException {
		return ResponseEntity.ok(aiService.getDescription(file));
	}
}
