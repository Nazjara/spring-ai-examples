package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;

/**
 * Tool (function) calling: the model can decide to call our Java code to fetch data
 * it does not have, such as the current weather.
 */
public interface AiService {
	/**
	 * Answers a weather-related question, calling the weather tool when needed.
	 *
	 * @param question e.g. {@code "What's the weather in Lviv?"}
	 * @return the model's answer, built from live weather data
	 */
	Answer getAnswer(Question question);
}
