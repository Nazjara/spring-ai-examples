package com.nazjara.service;

import com.nazjara.function.WeatherServiceFunction;
import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.model.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

	private final OpenAiChatClient openAiChatClient;

	@Value("${api-ninjas.api-key}")
	private String apiNinjasApiKey;

	@Override
	public Answer getAnswer(Question question) {
		var promptOptions = OpenAiChatOptions.builder()
			.withFunctionCallbacks(List.of(FunctionCallbackWrapper.builder(new WeatherServiceFunction(apiNinjasApiKey))
				.withName("WeatherFunction")
				.withDescription("Get a current weather for a location")
					.withResponseConverter(weatherResponse -> {
						var schema = ModelOptionsUtils.getJsonSchema(WeatherResponse.class, false);
						var json = ModelOptionsUtils.toJsonString(weatherResponse);
						return schema + "\n" + json;
					})
				.build()))
			.build();

		var systemMessage = new SystemPromptTemplate("When talking about weather, you'll receive sunrise and sunset as epoch time in GMT. " +
			"Please determine the timezone where the city is located and provide the local time for sunrise and sunset in format HH:mm:ss. " +
			"You can also explicitly mention the local timezone for a city.").createMessage();
		var message = new PromptTemplate(question.question()).createMessage();
		var response = openAiChatClient.call(new Prompt(List.of(systemMessage, message), promptOptions));
		return new Answer(response.getResult().getOutput().getContent());
	}
}
