package com.nazjara.rest;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import com.nazjara.model.Answer;
import com.nazjara.model.MemoryType;
import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * REST entry points for multi-turn chat. {@code {memoryType}} is {@code window} or
 * {@code vector} (converted by {@code MemoryTypeConverter}).
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final AiService aiService;

	/**
	 * Sends a message in a conversation.
	 *
	 * @param memoryType memory strategy
	 * @param conversationId conversation identifier
	 * @param question JSON body {@code {"question": "..."}}
	 * @return the model's reply
	 */
	@PostMapping("/{memoryType}/{conversationId}")
	public Answer chat(@PathVariable MemoryType memoryType, @PathVariable String conversationId, @RequestBody Question question) {
		return aiService.chat(memoryType, conversationId, question);
	}

	/**
	 * Sends a message and streams the reply as Server-Sent Events.
	 *
	 * @param memoryType memory strategy
	 * @param conversationId conversation identifier
	 * @param question JSON body {@code {"question": "..."}}
	 * @return reply fragments
	 */
	@PostMapping(value = "/{memoryType}/{conversationId}/stream", produces = TEXT_EVENT_STREAM_VALUE)
	public Flux<String> stream(@PathVariable MemoryType memoryType, @PathVariable String conversationId, @RequestBody Question question) {
		return aiService.stream(memoryType, conversationId, question);
	}

	/**
	 * Window strategy: the messages that will be resent with the next request.
	 *
	 * @param conversationId conversation identifier
	 * @return the stored message window
	 */
	@GetMapping("/window/{conversationId}")
	public List<String> history(@PathVariable String conversationId) {
		return aiService.history(conversationId);
	}

	/**
	 * Vector strategy: the past messages that would be retrieved for a query.
	 *
	 * @param conversationId conversation identifier
	 * @param query text to search with
	 * @return the most similar past messages
	 */
	@GetMapping("/vector/{conversationId}")
	public List<String> recall(@PathVariable String conversationId, @RequestParam String query) {
		return aiService.recall(conversationId, query);
	}

	/**
	 * Forgets a conversation.
	 *
	 * @param memoryType memory store to clear
	 * @param conversationId conversation identifier
	 */
	@DeleteMapping("/{memoryType}/{conversationId}")
	public void clear(@PathVariable MemoryType memoryType, @PathVariable String conversationId) {
		aiService.clear(memoryType, conversationId);
	}
}
