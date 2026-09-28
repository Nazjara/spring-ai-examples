package com.nazjara;

import com.anthropic.models.messages.OutputConfig;
import com.anthropic.models.messages.ThinkingConfigAdaptive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.ai.anthropic.AnthropicChatOptions;

/**
 * Reasoning is configured, not prompted. With adaptive thinking the model decides when and how
 * long to think before answering, and {@code effort} sets how much work it puts in overall
 * (thinking depth, length, number of tool calls): the lever for trading quality against cost and
 * latency. "Think step by step" in prompt text is redundant on current models.
 */
class ThinkingAndEffortTest extends BaseTestClass {

	private static final String PROBLEM = "In how many ways can a 3x8 board be tiled with 2x1 dominoes?";

	@Test
	void adaptiveThinkingExposesAReasoningSummary() {
		var response = chatClient.prompt()
			.options(AnthropicChatOptions.builder().thinkingAdaptive(ThinkingConfigAdaptive.Display.SUMMARIZED))
			.user(PROBLEM)
			.call()
			.chatResponse();

		// Spring AI returns each thinking block as its own generation, marked with the block's signature
		response.getResults().forEach(generation -> {
			var message = generation.getOutput();
			var label = message.getMetadata().containsKey("signature") ? "[thinking]" : "[answer]";
			System.out.println(label + "\n" + message.getText() + "\n");
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "low", "high" })
	void effortTradesThoroughnessForTokens(String effort) {
		var response = chatClient.prompt()
			.options(AnthropicChatOptions.builder().effort(OutputConfig.Effort.of(effort)))
			.user(PROBLEM)
			.call()
			.chatResponse();

		System.out.println("effort=%s outputTokens=%d%n%s".formatted(effort,
				response.getMetadata().getUsage().getCompletionTokens(), response.getResults().getLast().getOutput().getText()));
	}
}
