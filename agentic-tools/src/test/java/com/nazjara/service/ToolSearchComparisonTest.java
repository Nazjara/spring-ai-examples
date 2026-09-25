package com.nazjara.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nazjara.model.Question;
import com.nazjara.tool.TravelTools;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;

/**
 * Shows, without calling a real model, what each mode actually sends: a stub
 * {@link ChatModel} records the tool definitions attached to the first request.
 */
class ToolSearchComparisonTest {

	@Test
	void toolSearchSendsFewerToolDefinitionsThanAllTools() {
		var allTools = firstRequestToolNames(false);
		var toolSearch = firstRequestToolNames(true);

		System.out.println("all tools   -> " + allTools.size() + " definitions: " + allTools);
		System.out.println("tool search -> " + toolSearch.size() + " definitions: " + toolSearch);

		assertThat(allTools).hasSize(15).contains("searchFlights", "convertCurrency");
		assertThat(toolSearch).hasSizeLessThan(allTools.size()).doesNotContain("searchFlights");
	}

	private static List<String> firstRequestToolNames(boolean toolSearch) {
		var captured = new ArrayList<List<String>>();
		var stub = new ChatModel() {
			@Override
			public ChatResponse call(Prompt prompt) {
				captured.add(toolNames(prompt));
				return new ChatResponse(List.of(new Generation(new AssistantMessage("stub answer"))));
			}

			// Real models (Anthropic, OpenAI) expose tool-capable options; without this,
			// ChatClient has nowhere to put the tool definitions.
			@Override
			public ChatOptions getOptions() {
				return ToolCallingChatOptions.builder().build();
			}
		};
		var service = new AiServiceImpl(ChatClient.builder(stub), new TravelTools());
		service.ask(new Question("Find me a flight from Kyiv to Lisbon on 2026-10-10"), "test-user", toolSearch);
		return captured.getFirst();
	}

	private static List<String> toolNames(Prompt prompt) {
		if (!(prompt.getOptions() instanceof ToolCallingChatOptions options) || options.getToolCallbacks() == null) {
			return List.of();
		}
		return options.getToolCallbacks().stream().map(ToolCallback::getToolDefinition).map(d -> d.name()).sorted().toList();
	}
}
