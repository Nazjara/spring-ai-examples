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

/**
 * REST entry points for the basics examples. Each endpoint is one model call.
 */
@RestController
public class QuestionController {

	private final AiService aiService;

	public QuestionController(AiService aiService) {
		this.aiService = aiService;
	}

	/**
	 * Free-text question and answer.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the model's reply
	 */
	@PostMapping("/ask")
	public Answer askQuestion(@RequestBody Question question) {
		return aiService.getAnswer(question);
	}

	/**
	 * Structured output with a single field.
	 *
	 * @param country country name
	 * @return the capital city
	 */
	@GetMapping("/capital")
	public GetCapitalResponse getCapital(@RequestParam String country) {
		return aiService.getCapital(country);
	}

	/**
	 * Structured output with a richer typed record.
	 *
	 * @param country country name
	 * @return typed facts about the capital
	 */
	@GetMapping("/capital/details")
	public CapitalDetails getCapitalDetails(@RequestParam String country) {
		return aiService.getCapitalDetails(country);
	}

	/**
	 * Structured output as a list.
	 *
	 * @param region geographic region
	 * @return capital details for every country in the region
	 */
	@GetMapping("/capitals")
	public List<CapitalDetails> getCapitals(@RequestParam String region) {
		return aiService.getCapitals(region);
	}
}
