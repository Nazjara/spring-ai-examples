package com.nazjara.rest;

import com.nazjara.model.Question;
import com.nazjara.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {

	private final OpenAIService openAIService;

	@PostMapping(value = "/audio", produces = "audio/mpeg")
	public byte[] generateAudio(@RequestBody Question question) {
		return openAIService.getAudio(question);
	}
}
