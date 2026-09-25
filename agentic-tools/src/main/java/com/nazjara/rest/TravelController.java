package com.nazjara.rest;

import com.nazjara.model.AskResponse;
import com.nazjara.model.Question;
import com.nazjara.model.TripPlan;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry points for the travel agent.
 */
@RestController
@RequiredArgsConstructor
public class TravelController {

	private final AiService aiService;

	/**
	 * Travel question answered with tools. Run the same question with
	 * {@code toolSearch=false} and {@code toolSearch=true} and compare the token counts.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @param userId current user; bookings are made for this id
	 * @param toolSearch whether to use tool search
	 * @return answer and token usage
	 */
	@PostMapping("/ask")
	public AskResponse ask(@RequestBody Question question,
			@RequestHeader(value = "X-User-Id", defaultValue = "demo-user") String userId,
			@RequestParam(defaultValue = "false") boolean toolSearch) {
		return aiService.ask(question, userId, toolSearch);
	}

	/**
	 * Structured, schema-validated trip plan.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the trip plan
	 */
	@PostMapping("/plan")
	public TripPlan plan(@RequestBody Question question) {
		return aiService.plan(question);
	}
}
