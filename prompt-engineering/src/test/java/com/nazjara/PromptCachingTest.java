package com.nazjara;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.anthropic.AnthropicCacheOptions;
import org.springframework.ai.anthropic.AnthropicCacheStrategy;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.model.ChatResponse;

/**
 * Prompt caching is a prompt-design concern: the cache matches on an exact prefix
 * (tools, then system, then messages), so stable content goes first and anything that varies
 * per request goes last. {@link AnthropicCacheStrategy#SYSTEM_ONLY} places a cache breakpoint on
 * the system message; the first call writes it, later calls with the same prefix read it at a
 * fraction of the input price. Prefixes below the model's minimum (1024 tokens on Sonnet 5)
 * are silently not cached.
 */
class PromptCachingTest extends BaseTestClass {

	private static final String SYSTEM_PROMPT = """
			You answer questions from the product team about customer reviews. Base every answer only \
			on the reviews below and cite review indexes.

			%s""".formatted(Reviews.asXml(Reviews.TUMBLER));

	@Test
	void stablePrefixIsReadFromCache() {
		var first = ask("Which complaints should the product team fix first?");
		var second = ask("Which features do satisfied customers mention?");

		print(first);
		print(second);
		assertThat(second.getMetadata().getUsage().getCacheReadInputTokens()).isPositive();
	}

	private ChatResponse ask(String question) {
		return chatClient.prompt()
			.options(AnthropicChatOptions.builder()
				.cacheOptions(AnthropicCacheOptions.builder().strategy(AnthropicCacheStrategy.SYSTEM_ONLY).build()))
			.system(SYSTEM_PROMPT)
			.user(question)
			.call()
			.chatResponse();
	}

	private void print(ChatResponse response) {
		var usage = response.getMetadata().getUsage();
		System.out.println("input=%d cacheWrite=%s cacheRead=%s%n%s%n".formatted(usage.getPromptTokens(),
				usage.getCacheWriteInputTokens(), usage.getCacheReadInputTokens(), response.getResult().getOutput().getText()));
	}
}
