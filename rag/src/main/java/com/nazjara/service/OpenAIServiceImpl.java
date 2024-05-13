package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;
	private final SimpleVectorStore simpleVectorStore;

	@Value("classpath:/template/rag-prompt-template-meta.st")
	private Resource ragPromptTemplate;

	@Override
	public Answer getAnswer(Question question) {
		var documents = simpleVectorStore.similaritySearch(SearchRequest.query(question.question()).withTopK(4));
		var contentList = documents.stream().map(Document::getContent).toList();

		var promptTemplate = new PromptTemplate(ragPromptTemplate);
		var prompt = promptTemplate.create(Map.of("input", question.question(),
			"documents", String.join("\n", contentList)));

		var response = chatClient.call(prompt);
		return new Answer(response.getResult().getOutput().getContent());
	}
}
