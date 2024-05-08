package com.nazjara.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;

	public OpenAIServiceImpl(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@Override
	public String getAnswer(String question) {
		var promptTemplate = new PromptTemplate(question);
		var prompt = promptTemplate.create();
		var response = chatClient.call(prompt);

		return response.getResult().getOutput().getContent();
	}
}
