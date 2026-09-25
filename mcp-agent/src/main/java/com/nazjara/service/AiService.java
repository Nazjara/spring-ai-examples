package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;

/**
 * Uses the three MCP primitives exposed by the {@code mcp-server} module, each driven by a
 * different party:
 * <ul>
 *   <li><b>tools</b>, chosen by the model;</li>
 *   <li><b>resources</b>, chosen by the application;</li>
 *   <li><b>prompts</b>, chosen by the user.</li>
 * </ul>
 */
public interface AiService {

	/**
	 * Answers a question; the model may call the remote MCP tools (e.g. {@code currentWeather}).
	 *
	 * @param question the user's question
	 * @return the model's answer
	 */
	Answer ask(Question question);

	/**
	 * Answers a question about a movie. The application reads the {@code movies://{title}}
	 * resource and attaches it as context before calling the model.
	 *
	 * @param title movie title
	 * @param question the user's question about the movie
	 * @return the model's answer
	 */
	Answer askAboutMovie(String title, Question question);

	/**
	 * Fetches the server's {@code weather-report} prompt for a city and runs it, with the
	 * MCP tools available so the model can look up the actual weather.
	 *
	 * @param city city name
	 * @param country country name
	 * @return the generated weather report
	 */
	Answer weatherReport(String city, String country);
}
