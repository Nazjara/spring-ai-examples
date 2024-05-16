package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.nazjara.service.OpenAIService;

@RestController
public class QuestionController {

	private final OpenAIService openAIService;

	public QuestionController(OpenAIService openAIService) {
		this.openAIService = openAIService;
	}

	@PostMapping("/weather")
	public Answer askQuestion(@RequestBody Question question) {
		return openAIService.getAnswer(question);
	}
}
