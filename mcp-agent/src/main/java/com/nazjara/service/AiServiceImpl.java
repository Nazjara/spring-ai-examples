package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.GetPromptRequest;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

/**
 * {@link AiService} backed by Claude and the MCP server configured under
 * {@code spring.ai.mcp.client.streamable-http.connections}.
 *
 * <p>In MCP terms this application is the <b>host</b>: it talks to the user and the model.
 * The <b>MCP client</b> is not a separate app but a component inside it, created by
 * {@code spring-ai-starter-mcp-client}, one per configured server. At startup the client
 * connects, performs the {@code initialize} handshake and fetches the server's tool list.
 * Spring AI then exposes:
 * <ul>
 *   <li>a {@link ToolCallbackProvider} that wraps every remote MCP tool as a regular Spring
 *       AI {@code ToolCallback}. To {@link ChatClient} it looks exactly like a local tool;
 *       the call just travels over HTTP to the server.</li>
 *   <li>an {@link McpSyncClient} per connection, for direct access to resources and prompts,
 *       which are not tools and must be fetched by the application itself.</li>
 * </ul>
 *
 * <p>Sequence of {@link #ask} for "What's the weather in Lviv?":
 * <ol start="0">
 *   <li>(startup) MCP client → server: {@code initialize}, {@code tools/list}.</li>
 *   <li>User → this app: the question.</li>
 *   <li>This app → Claude: the question plus the tool definitions.</li>
 *   <li>Claude → this app: a tool call, {@code currentWeather(Lviv, Ukraine)}, not text.</li>
 *   <li>{@code ToolCallingAdvisor} runs the tool callback → MCP client → server
 *       ({@code tools/call}) → API Ninjas, and the result comes back the same way.</li>
 *   <li>This app → Claude: the tool result. Claude writes the final answer, which goes back
 *       to the user.</li>
 * </ol>
 * The model never contacts the server itself; it only asks the host to call a tool.
 */
@Service
public class AiServiceImpl implements AiService {

	private final ChatClient chatClient;
	private final McpSyncClient mcpClient;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools, List<McpSyncClient> mcpClients) {
		this.chatClient = chatClientBuilder.defaultTools(mcpTools).build();
		this.mcpClient = mcpClients.getFirst();
	}

	@Override
	public Answer ask(Question question) {
		return new Answer(chatClient.prompt().user(question.question()).call().content());
	}

	@Override
	public Answer askAboutMovie(String title, Question question) {
		var result = mcpClient.readResource(new ReadResourceRequest("movies://" + title));
		var movieJson = result.contents().stream()
			.filter(TextResourceContents.class::isInstance)
			.map(content -> ((TextResourceContents) content).text())
			.collect(Collectors.joining("\n"));

		var content = chatClient.prompt()
			.system(s -> s.text("Answer questions about this movie using only the data below.\n\n{movie}").param("movie", movieJson))
			.user(question.question())
			.call()
			.content();
		return new Answer(content);
	}

	@Override
	public Answer weatherReport(String city, String country) {
		var prompt = mcpClient.getPrompt(new GetPromptRequest("weather-report", Map.of("city", city, "country", country)));
		var messages = prompt.messages().stream().map(AiServiceImpl::toSpringAiMessage).toList();
		return new Answer(chatClient.prompt().messages(messages).call().content());
	}

	/**
	 * Converts an MCP prompt message into the equivalent Spring AI message.
	 */
	private static Message toSpringAiMessage(PromptMessage message) {
		var text = message.content() instanceof TextContent textContent ? textContent.text() : "";
		return message.role() == Role.ASSISTANT ? new AssistantMessage(text) : new UserMessage(text);
	}
}
