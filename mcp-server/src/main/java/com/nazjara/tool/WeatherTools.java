package com.nazjara.tool;

import com.nazjara.client.WeatherClient;
import com.nazjara.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;

/**
 * MCP <b>tools</b>: actions the model can decide to invoke, the MCP equivalent of
 * {@code @Tool} / {@code FunctionToolCallback} in the {@code functions} module.
 *
 * <p>Spring AI scans {@link McpTool} methods at startup and publishes them through the
 * server's {@code tools/list} endpoint, with an input JSON schema generated from the
 * method parameters. Any MCP host can call them through its embedded MCP client: the {@code mcp-agent}
 * module, Claude Code or Claude Desktop, with no Java-specific integration.
 */
@Component
@RequiredArgsConstructor
public class WeatherTools {

	private final WeatherClient weatherClient;

	/**
	 * Returns the current weather for a city.
	 *
	 * <p>{@link McpSyncRequestContext} is injected by Spring AI and is not part of the tool's
	 * input schema. It lets the tool talk back to the client while running: here it sends a
	 * log notification the client can display.
	 *
	 * <p>The {@code annotations} are hints for clients: a read-only, non-destructive tool is
	 * safe to run without asking the user first. Without them, the MCP defaults assume the
	 * tool may be destructive.
	 *
	 * @param context per-request MCP context (logging, progress, sampling, elicitation)
	 * @param city city chosen by the model
	 * @param country country chosen by the model
	 * @return current weather
	 */
	@McpTool(name = "currentWeather", title = "Current weather",
		description = "Get the current weather for a city. Sunrise and sunset are epoch seconds in GMT.",
		annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false, idempotentHint = true))
	public WeatherResponse currentWeather(McpSyncRequestContext context,
			@McpToolParam(description = "City name, e.g. Lviv") String city,
			@McpToolParam(description = "Country name, e.g. Ukraine") String country) {
		context.info("Fetching weather for " + city + ", " + country);
		return weatherClient.currentWeather(city, country);
	}
}
