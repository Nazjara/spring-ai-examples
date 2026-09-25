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

/**
 * {@link AiService} backed by a {@link ChatClient}.
 *
 * <p>How structured output works here: {@code .call().entity(Type.class)} generates a JSON
 * schema from the target type (field names, types and {@code @JsonPropertyDescription}
 * texts), appends "respond with JSON matching this schema" to the prompt, and parses the
 * reply back into {@code Type}. For generic types such as {@code List<CapitalDetails>},
 * a {@link ParameterizedTypeReference} carries the type information that erasure would
 * otherwise lose.
 *
 * <p>Prompts live in {@code src/main/resources/templates/*.st} (StringTemplate syntax);
 * {@code {country}} / {@code {region}} are filled in via {@code .param(...)}.
 */
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
