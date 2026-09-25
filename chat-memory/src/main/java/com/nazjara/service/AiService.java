package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.MemoryType;
import com.nazjara.model.Question;
import java.util.List;
import reactor.core.publisher.Flux;

/**
 * Multi-turn chat with two interchangeable memory strategies, see {@link MemoryType}.
 *
 * <p>Models are stateless: "memory" means the application stores past messages and adds
 * relevant ones to every new request.
 */
public interface AiService {
	/**
	 * Sends one message in a conversation and returns the whole reply.
	 *
	 * @param memoryType which memory strategy to use
	 * @param conversationId identifies the conversation; required by Spring AI 2.0
	 * @param question the new user message
	 * @return the model's reply
	 */
	Answer chat(MemoryType memoryType, String conversationId, Question question);
	/**
	 * Same as {@link #chat}, but streams the reply token by token.
	 *
	 * @param memoryType which memory strategy to use
	 * @param conversationId identifies the conversation
	 * @param question the new user message
	 * @return the reply as a stream of text fragments
	 */
	Flux<String> stream(MemoryType memoryType, String conversationId, Question question);
	/**
	 * Returns what the window strategy will resend with the next request.
	 *
	 * @param conversationId identifies the conversation
	 * @return up to the last 20 messages, formatted as {@code TYPE: text}
	 */
	List<String> history(String conversationId);
	/**
	 * Shows what the vector strategy would retrieve for a given query.
	 *
	 * @param conversationId identifies the conversation
	 * @param query text to search the conversation's past messages with
	 * @return the most similar past messages
	 */
	List<String> recall(String conversationId, String query);
	/**
	 * Forgets a conversation in the given memory store.
	 *
	 * @param memoryType which store to clear
	 * @param conversationId identifies the conversation
	 */
	void clear(MemoryType memoryType, String conversationId);
}
