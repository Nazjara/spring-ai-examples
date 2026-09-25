package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.nazjara.service.AiService;

@RestController
public class QuestionController {

	private final AiService aiService;

	public QuestionController(AiService aiService) {
		this.aiService = aiService;
	}

	@PostMapping("/weather")
	public Answer askQuestion(@RequestBody Question question) {
		return aiService.getAnswer(question);
	}
}
