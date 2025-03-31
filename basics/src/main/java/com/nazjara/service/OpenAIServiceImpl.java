package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;

	public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Value("classpath:templates/get-capital-prompt.st")
	private Resource getCapitalPrompt;

	@Value("classpath:templates/get-capital-extended-prompt.st")
	private Resource getCapitalExtendedPrompt;

	@Override
	public Answer getAnswer(Question question) {
		var promptTemplate = new PromptTemplate(question.question());
		var prompt = promptTemplate.create();
		return new Answer(this.getAnswer(prompt));
	}

	@Override
	public GetCapitalResponse getCapital(String country, boolean extended) {
		var converter = new BeanOutputConverter<>(GetCapitalResponse.class);
		var format = converter.getFormat();

		var promptTemplate = extended ? new PromptTemplate(getCapitalExtendedPrompt) : new PromptTemplate(getCapitalPrompt);
		var prompt = promptTemplate.create(Map.of("country", country, "format", format));
		return converter.convert(getAnswer(prompt));
	}

	private String getAnswer(Prompt prompt) {
		return chatClient.prompt(prompt).call().content();
	}
}
