package com.nazjara.service;

import com.nazjara.model.AskResponse;
import com.nazjara.model.Question;
import com.nazjara.model.TripPlan;

/**
 * Agentic tool calling with Spring AI 2.0: the model plans multi-step work over a set of
 * {@code @Tool} methods, and the application controls cost, limits and output quality.
 */
public interface AiService {

	/**
	 * Answers a travel question, letting the model call as many tools as it needs.
	 *
	 * @param question the user's question
	 * @param userId current user, passed to tools via {@code ToolContext} and never shown
	 *               to the model
	 * @param toolSearch {@code true} to reveal tools on demand through tool search,
	 *                   {@code false} to send all tool definitions with every request
	 * @return the answer plus token usage, for comparing the two modes
	 */
	AskResponse ask(Question question, String userId, boolean toolSearch);

	/**
	 * Builds a structured multi-day trip plan. The model gathers data with tools, and its
	 * JSON output is validated against the {@link TripPlan} schema, with automatic retries.
	 *
	 * @param question trip request, e.g. "3 days in Lisbon from 2026-10-10, flying from Kyiv"
	 * @return the validated plan
	 */
	TripPlan plan(Question question);
}
