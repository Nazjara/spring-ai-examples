package com.nazjara.rest;

import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {

	private final AiService aiService;

	@PostMapping(value = "/audio", produces = "audio/mpeg")
	public byte[] generateAudio(@RequestBody Question question) {
		return aiService.getAudio(question);
	}
}
