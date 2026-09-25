package com.nazjara.prompt;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

/**
 * MCP <b>prompts</b>: reusable, parameterised prompt templates published by the server.
 * The user usually picks them; Claude Code, for example, shows them as slash commands.
 * The server fills in the arguments and returns ready-to-send messages.
 */
@Component
public class WeatherPrompts {

	/**
	 * Builds a prompt asking for a friendly weather report, which leads the model to call
	 * the {@code currentWeather} tool.
	 *
	 * @param city city to report on
	 * @param country country of the city
	 * @return a single user message
	 */
	@McpPrompt(name = "weather-report", description = "Friendly weather report for a city, with local sunrise and sunset times")
	public GetPromptResult weatherReport(@McpArg(name = "city", description = "City name", required = true) String city,
			@McpArg(name = "country", description = "Country name", required = true) String country) {
		var text = "Give me a short, friendly weather report for " + city + ", " + country + ". "
			+ "Convert sunrise and sunset to local time in the city's timezone (HH:mm).";
		return new GetPromptResult("Weather report for " + city,
			List.of(new PromptMessage(Role.USER, TextContent.builder(text).build())));
	}
}
