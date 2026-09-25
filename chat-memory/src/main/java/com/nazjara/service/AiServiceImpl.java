package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.MemoryType;
import com.nazjara.model.Question;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * {@link AiService} with one {@link ChatClient} per {@link MemoryType}; each has a
 * different memory advisor. Advisors wrap every call: they can change the request before
 * it reaches the model and see the response afterwards.
 * <ul>
 *   <li>{@link MessageChatMemoryAdvisor} (window): loads the conversation from
 *       {@link ChatMemory} and adds it as chat history, then saves the new question and
 *       answer.</li>
 *   <li>{@link VectorStoreChatMemoryAdvisor} (vector): searches the {@link VectorStore}
 *       for the {@code VECTOR_MEMORY_TOP_K} (6) past messages most similar to the new
 *       question and adds them to the system prompt, then stores the new messages as
 *       vectors.</li>
 *   <li>{@link SimpleLoggerAdvisor}: logs the final request at DEBUG, so the difference
 *       between the two strategies is visible in the console.</li>
 * </ul>
 * The conversation id is passed per request via {@link ChatMemory#CONVERSATION_ID}.
 */
@Service
public class AiServiceImpl implements AiService {

	private static final String SYSTEM_PROMPT = "You are a friendly assistant. Keep answers short and refer back to earlier messages when relevant.";
	private static final int VECTOR_MEMORY_TOP_K = 6;

	private final Map<MemoryType, ChatClient> chatClients;
	private final ChatMemory chatMemory;
	private final VectorStore vectorStore;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, VectorStore vectorStore) {
		this.chatMemory = chatMemory;
		this.vectorStore = vectorStore;
		this.chatClients = Map.of(
			MemoryType.WINDOW, chatClientBuilder.clone()
				.defaultSystem(SYSTEM_PROMPT)
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor())
				.build(),
			MemoryType.VECTOR, chatClientBuilder.clone()
				.defaultSystem(SYSTEM_PROMPT)
				.defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(vectorStore).defaultTopK(VECTOR_MEMORY_TOP_K).build(), new SimpleLoggerAdvisor())
				.build());
	}

	@Override
	public Answer chat(MemoryType memoryType, String conversationId, Question question) {
		var content = prompt(memoryType, conversationId, question).call().content();
		return new Answer(content);
	}

	@Override
	public Flux<String> stream(MemoryType memoryType, String conversationId, Question question) {
		return prompt(memoryType, conversationId, question).stream().content();
	}

	@Override
	public List<String> history(String conversationId) {
		return chatMemory.get(conversationId).stream()
			.map(message -> message.getMessageType() + ": " + message.getText())
			.toList();
	}

	@Override
	public List<String> recall(String conversationId, String query) {
		var request = SearchRequest.builder()
			.query(query)
			.topK(VECTOR_MEMORY_TOP_K)
			.filterExpression(conversationFilter(conversationId))
			.build();
		return vectorStore.similaritySearch(request).stream()
			.map(Document::getText)
			.toList();
	}

	@Override
	public void clear(MemoryType memoryType, String conversationId) {
		switch (memoryType) {
			case WINDOW -> chatMemory.clear(conversationId);
			case VECTOR -> vectorStore.delete(conversationFilter(conversationId));
		}
	}

	private ChatClient.ChatClientRequestSpec prompt(MemoryType memoryType, String conversationId, Question question) {
		return chatClients.get(memoryType).prompt()
			.user(question.question())
			.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId));
	}

	/**
	 * Builds a typed vector-store filter for one conversation's messages. The advisor tags
	 * every stored message with a {@code conversationId} metadata key. The builder escapes the
	 * value, unlike string concatenation, which would allow filter injection.
	 */
	private static Filter.Expression conversationFilter(String conversationId) {
		return new FilterExpressionBuilder().eq("conversationId", conversationId).build();
	}
}
