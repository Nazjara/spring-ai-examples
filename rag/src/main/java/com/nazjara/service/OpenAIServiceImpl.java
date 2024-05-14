package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;
	private final VectorStore vectorStore;

	@Value("classpath:/template/rag-prompt-template.st")
	private Resource ragPromptTemplate;

	@Value("classpath:/template/rag-prompt-template-detailed.st")
	private Resource ragPromptTemplateDetailed;

	@Value("classpath:/template/system-message.st")
	private Resource systemMessage;

	@Override
	public Answer getAnswer(Question question) {
		var systemPromptTemplate = new SystemPromptTemplate(systemMessage);
		var systemMessage = systemPromptTemplate.createMessage();

		var documents = vectorStore.similaritySearch(SearchRequest.query(question.question()).withTopK(4));
		var contentList = documents.stream().map(Document::getContent).toList();
		var promptTemplate = new PromptTemplate(ragPromptTemplate);
		var userMessage = promptTemplate.createMessage(Map.of("input", question.question(),
			"documents", String.join("\n", contentList)));

		var response = chatClient.call(new Prompt(List.of(systemMessage, userMessage)));
		return new Answer(response.getResult().getOutput().getContent());
	}
}
