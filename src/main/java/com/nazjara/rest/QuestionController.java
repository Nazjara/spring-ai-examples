package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.service.OpenAIService;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuestionController {

	private final OpenAIService openAIService;

	public QuestionController(OpenAIService openAIService) {
		this.openAIService = openAIService;
	}

	@PostMapping("/ask")
	public Answer askQuestion(@RequestBody Question question) {
		return openAIService.getAnswer(question);
	}

	@GetMapping("/capital")
	public Answer getCapital(@RequestParam String country) {
		return openAIService.getCapital(country);
	}
}
