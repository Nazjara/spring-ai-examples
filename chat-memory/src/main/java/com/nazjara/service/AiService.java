package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.MemoryType;
import com.nazjara.model.Question;
import java.util.List;
import reactor.core.publisher.Flux;

public interface AiService {
	Answer chat(MemoryType memoryType, String conversationId, Question question);
	Flux<String> stream(MemoryType memoryType, String conversationId, Question question);
	List<String> history(String conversationId);
	List<String> recall(String conversationId, String query);
	void clear(MemoryType memoryType, String conversationId);
}
