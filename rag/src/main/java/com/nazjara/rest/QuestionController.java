package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for the RAG example.
 */
@RestController
public class QuestionController {

	private final AiService aiService;

	public QuestionController(AiService aiService) {
		this.aiService = aiService;
	}

	/**
	 * Answers a question from the loaded documents.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the grounded answer
	 */
	@PostMapping("/ask")
	public Answer askQuestion(@RequestBody Question question) {
		return aiService.getAnswer(question);
	}
}
