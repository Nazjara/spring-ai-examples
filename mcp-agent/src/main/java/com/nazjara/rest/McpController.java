package com.nazjara.rest;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry points exercising the MCP server's tools, resources and prompts through Claude.
 * The {@code mcp-server} module must be running on port 8090.
 */
@RestController
@RequiredArgsConstructor
public class McpController {

	private final AiService aiService;

	/**
	 * Free-form question; Claude calls remote MCP tools when it needs them.
	 *
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the answer
	 */
	@PostMapping("/ask")
	public Answer ask(@RequestBody Question question) {
		return aiService.ask(question);
	}

	/**
	 * Question about a movie, answered from the {@code movies://{title}} MCP resource.
	 *
	 * @param title movie title, e.g. {@code Avatar}
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the answer
	 */
	@PostMapping("/movies/{title}/ask")
	public Answer askAboutMovie(@PathVariable String title, @RequestBody Question question) {
		return aiService.askAboutMovie(title, question);
	}

	/**
	 * Runs the server-provided {@code weather-report} MCP prompt.
	 *
	 * @param city city name
	 * @param country country name
	 * @return the weather report
	 */
	@GetMapping("/weather-report")
	public Answer weatherReport(@RequestParam String city, @RequestParam String country) {
		return aiService.weatherReport(city, country);
	}
}
