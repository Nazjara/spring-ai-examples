package com.nazjara.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that the model actually calls an MCP tool, rather than answering from memory.
 *
 * <p>Every MCP tool callback is wrapped in a {@link CountingToolCallback} that counts its
 * invocations before delegating to the MCP client. A positive count proves the full path
 * ran: the model requested the tool, and the call went over MCP ({@code tools/call}) to the
 * server. A reworded tool description that stops the model from choosing the tool fails
 * this test.
 *
 * <p>Tool names are matched with {@code endsWith} because Spring AI may prefix MCP tool names
 * to keep them unique across servers.
 *
 * <p>Runs only when {@code ANTHROPIC_API_KEY} is set and {@code mcp-server} is running on
 * port 8090: the MCP client connects at startup, so the context cannot start without it.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
@EnabledIf("mcpServerRunning")
class McpToolCallTest {

	@Autowired
	ChatModel chatModel;

	@Autowired
	ToolCallbackProvider mcpTools;

	@Test
	void weatherQuestionCallsCurrentWeatherTool() {
		var spies = Arrays.stream(mcpTools.getToolCallbacks())
			.map(callback -> new CountingToolCallback(callback, new AtomicInteger()))
			.toList();

		var answer = ChatClient.builder(chatModel).build()
			.prompt()
			.user("What's the weather in Lviv, Ukraine right now?")
			.tools(spies.toArray())
			.call()
			.content();

		assertThat(spies)
			.filteredOn(spy -> spy.getToolDefinition().name().endsWith("currentWeather"))
			.singleElement()
			.satisfies(spy -> assertThat(spy.calls()).as("answer: %s", answer).hasPositiveValue());
	}

	static boolean mcpServerRunning() {
		try (var socket = new Socket()) {
			socket.connect(new InetSocketAddress("localhost", 8090), 500);
			return true;
		}
		catch (IOException _) {
			return false;
		}
	}

	private record CountingToolCallback(ToolCallback delegate, AtomicInteger calls) implements ToolCallback {

		@Override
		public ToolDefinition getToolDefinition() {
			return delegate.getToolDefinition();
		}

		@Override
		public ToolMetadata getToolMetadata() {
			return delegate.getToolMetadata();
		}

		@Override
		public String call(String toolInput) {
			calls.incrementAndGet();
			return delegate.call(toolInput);
		}

		@Override
		public String call(String toolInput, ToolContext toolContext) {
			calls.incrementAndGet();
			return delegate.call(toolInput, toolContext);
		}
	}
}
