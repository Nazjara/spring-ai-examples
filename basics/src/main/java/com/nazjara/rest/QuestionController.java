package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.CapitalDetails;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public GetCapitalResponse getCapital(@RequestParam String country) {
		return aiService.getCapital(country);
	}

	@GetMapping("/capital/details")
	public CapitalDetails getCapitalDetails(@RequestParam String country) {
		return aiService.getCapitalDetails(country);
	}

	@GetMapping("/capitals")
	public List<CapitalDetails> getCapitals(@RequestParam String region) {
		return aiService.getCapitals(region);
	}
}
