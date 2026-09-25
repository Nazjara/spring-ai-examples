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

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final AiService aiService;

	@PostMapping("/{memoryType}/{conversationId}")
	public Answer chat(@PathVariable MemoryType memoryType, @PathVariable String conversationId, @RequestBody Question question) {
		return aiService.chat(memoryType, conversationId, question);
	}

	@PostMapping(value = "/{memoryType}/{conversationId}/stream", produces = TEXT_EVENT_STREAM_VALUE)
	public Flux<String> stream(@PathVariable MemoryType memoryType, @PathVariable String conversationId, @RequestBody Question question) {
		return aiService.stream(memoryType, conversationId, question);
	}

	@GetMapping("/window/{conversationId}")
	public List<String> history(@PathVariable String conversationId) {
		return aiService.history(conversationId);
	}

	@GetMapping("/vector/{conversationId}")
	public List<String> recall(@PathVariable String conversationId, @RequestParam String query) {
		return aiService.recall(conversationId, query);
	}

	@DeleteMapping("/{memoryType}/{conversationId}")
	public void clear(@PathVariable MemoryType memoryType, @PathVariable String conversationId) {
		aiService.clear(memoryType, conversationId);
	}
}
