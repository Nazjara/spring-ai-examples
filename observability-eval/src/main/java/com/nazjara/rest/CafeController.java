package com.nazjara.rest;

import com.nazjara.model.AnswerWithSources;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for the café assistant. Each call produces one trace in Grafana (Tempo).
 */
@RestController
@RequiredArgsConstructor
public class CafeController {

	private final AiService aiService;

	/**
	 * Answers a guest question.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the answer plus the knowledge-base chunks used
	 */
	@PostMapping("/ask")
	public AnswerWithSources ask(@RequestBody Question question) {
		return aiService.ask(question);
	}
}
