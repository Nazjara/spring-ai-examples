package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
public class QuestionController {

	private final AiService aiService;

	public QuestionController(AiService aiService) {
		this.aiService = aiService;
	}

	@PostMapping("/ask")
	public Answer askQuestion(@RequestBody Question question) {
		return aiService.getAnswer(question);
	}

	@GetMapping("/capital")
	public GetCapitalResponse getCapital(@RequestParam String country, @RequestParam boolean extended) {
		return aiService.getCapital(country, extended);
	}
}
