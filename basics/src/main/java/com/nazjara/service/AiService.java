package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.CapitalDetails;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import java.util.List;

/**
 * Entry-level Spring AI operations: a plain chat call and structured output.
 *
 * <p>Every method is a single, stateless request to the chat model — nothing is remembered
 * between calls (see the {@code chat-memory} module for that).
 */
public interface AiService {
	/**
	 * Sends the question to the model as-is and returns its free-text reply.
	 *
	 * @param question the user's question
	 * @return the model's answer as plain text
	 */
	Answer getAnswer(Question question);
	/**
	 * Asks for the capital of a country and maps the reply onto a one-field record.
	 *
	 * @param country country name, e.g. {@code "France"}
	 * @return the capital city
	 */
	GetCapitalResponse getCapital(String country);
	/**
	 * Asks for several facts about a country's capital and maps them onto a typed record.
	 *
	 * <p>Shows that structured output is more than a string: numbers ({@code population}) and
	 * multiple fields come back already parsed.
	 *
	 * @param country country name
	 * @return typed facts about the capital
	 */
	CapitalDetails getCapitalDetails(String country);
	/**
	 * Asks for the capitals of all countries in a region and maps the reply onto a list.
	 *
	 * @param region a geographic region, e.g. {@code "Scandinavia"}
	 * @return one entry per country in the region
	 */
	List<CapitalDetails> getCapitals(String region);
}
