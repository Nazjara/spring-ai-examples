package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.CapitalDetails;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

	private final ChatClient chatClient;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Value("classpath:templates/get-capital-prompt.st")
	private Resource getCapitalPrompt;

	@Value("classpath:templates/get-capital-details-prompt.st")
	private Resource getCapitalDetailsPrompt;

	@Value("classpath:templates/get-capitals-prompt.st")
	private Resource getCapitalsPrompt;

	@Override
	public Answer getAnswer(Question question) {
		return new Answer(chatClient.prompt().user(question.question()).call().content());
	}

	@Override
	public GetCapitalResponse getCapital(String country) {
		return chatClient.prompt()
			.user(u -> u.text(getCapitalPrompt).param("country", country))
			.call()
			.entity(GetCapitalResponse.class);
	}

	@Override
	public CapitalDetails getCapitalDetails(String country) {
		return chatClient.prompt()
			.user(u -> u.text(getCapitalDetailsPrompt).param("country", country))
			.call()
			.entity(CapitalDetails.class);
	}

	@Override
	public List<CapitalDetails> getCapitals(String region) {
		return chatClient.prompt()
			.user(u -> u.text(getCapitalsPrompt).param("region", region))
			.call()
			.entity(new ParameterizedTypeReference<>() {});
	}
}
