package com.nazjara.rest;

import com.nazjara.model.Question;
import com.nazjara.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@RestController
@RequiredArgsConstructor
public class QuestionController {

	private final OpenAIService openAIService;

	@PostMapping(value = "/image", produces = IMAGE_PNG_VALUE)
	public byte[] getImage(@RequestBody Question question) {
		return openAIService.getImage(question);
	}

	@PostMapping(value = "/vision", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> upload(@RequestParam MultipartFile file, @RequestParam String name) {
		return ResponseEntity.ok(openAIService.getDescription(file));
	}
}
