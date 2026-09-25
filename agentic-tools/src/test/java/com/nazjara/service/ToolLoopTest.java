package com.nazjara.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nazjara.model.Question;
import com.nazjara.tool.TravelTools;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

/**
 * Walks through one iteration of the tool loop with a scripted model:
 * <ol>
 *   <li>the model replies with a tool call ({@code bookFlight}) instead of text;</li>
 *   <li>{@code ToolCallingAdvisor} executes {@link TravelTools#bookFlight}, filling
 *       {@code ToolContext} with the user id supplied by the application;</li>
 *   <li>the tool result is sent back to the model, which then answers with text.</li>
 * </ol>
 */
class ToolLoopTest {

	@Test
	void toolCallIsExecutedWithApplicationProvidedToolContext() {
		var requests = new ArrayList<Prompt>();
		var model = new ChatModel() {
			@Override
			public ChatResponse call(Prompt prompt) {
				requests.add(prompt);
				var reply = requests.size() == 1
					? AssistantMessage.builder()
						.toolCalls(List.of(new ToolCall("call-1", "function", "bookFlight", "{\"flightNumber\":\"SA123\"}")))
						.build()
					: new AssistantMessage("Your flight is booked.");
				return new ChatResponse(List.of(new Generation(reply)));
			}

			@Override
			public ChatOptions getOptions() {
				return ToolCallingChatOptions.builder().build();
			}
		};

		var answer = new AiServiceImpl(ChatClient.builder(model), new TravelTools())
			.ask(new Question("Book flight SA123"), "alice", false);

		assertThat(requests).hasSize(2);
		var toolResult = requests.get(1).getInstructions().stream()
			.filter(ToolResponseMessage.class::isInstance)
			.map(ToolResponseMessage.class::cast)
			.flatMap(message -> message.getResponses().stream())
			.findFirst()
			.orElseThrow();
		System.out.println("tool result sent back to the model: " + toolResult.responseData());

		assertThat(toolResult.name()).isEqualTo("bookFlight");
		assertThat(toolResult.responseData()).contains("SA123").contains("alice");
		assertThat(answer.answer()).isEqualTo("Your flight is booked.");
	}
}
