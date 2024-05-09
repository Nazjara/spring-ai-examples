package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;

	public OpenAIServiceImpl(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@Value("classpath:templates/get-capital-prompt.st")
	private Resource getCapitalPrompt;

	@Override
	public Answer getAnswer(Question question) {
		var promptTemplate = new PromptTemplate(question.question());
		var prompt = promptTemplate.create();
		return new Answer(this.getAnswer(prompt));
	}

	@Override
	public Answer getCapital(String country) {
		var promptTemplate = new PromptTemplate(getCapitalPrompt);
		var prompt = promptTemplate.create(Map.of("country", country));
		return new Answer(this.getAnswer(prompt));
	}

	private String getAnswer(Prompt prompt) {
		var response = chatClient.call(prompt);
		return response.getResult().getOutput().getContent();
	}
}
