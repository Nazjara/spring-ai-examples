package com.nazjara.configuration;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the window memory used by the {@code window} strategy.
 */
@Configuration
public class ChatMemoryConfig {

	/**
	 * A {@link MessageWindowChatMemory} keeping the last 20 messages per conversation.
	 *
	 * <p>Storage comes from the auto-configured JDBC {@link ChatMemoryRepository}
	 * (table {@code spring_ai_chat_memory} in Postgres). When a conversation exceeds 20
	 * messages, the oldest are evicted from the database too.
	 *
	 * @param chatMemoryRepository JDBC-backed message storage
	 * @return the chat memory
	 */
	@Bean
	ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
		return MessageWindowChatMemory.builder()
			.chatMemoryRepository(chatMemoryRepository)
			.maxMessages(20)
			.build();
	}
}
