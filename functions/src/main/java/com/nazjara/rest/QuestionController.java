package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.nazjara.service.AiService;

/**
 * REST entry point for the tool-calling example.
 */
@RestController
public class QuestionController {

	private final AiService aiService;

	public QuestionController(AiService aiService) {
		this.aiService = aiService;
	}

	/**
	 * Answers a weather question using the weather tool.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the model's answer
	 */
	@PostMapping("/weather")
	public Answer askQuestion(@RequestBody Question question) {
		return aiService.getAnswer(question);
	}
}
