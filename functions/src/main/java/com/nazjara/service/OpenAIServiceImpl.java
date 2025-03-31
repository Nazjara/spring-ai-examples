package com.nazjara.service;

import com.nazjara.function.WeatherServiceFunction;
import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.model.WeatherRequest;
import com.nazjara.model.WeatherResponse;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIService {

	private final ChatClient chatClient;

	public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@Value("${api-ninjas.api-key}")
	private String apiNinjasApiKey;

	@Override
	public Answer getAnswer(Question question) {
		var toolCallback = FunctionToolCallback
				.builder("currentWeather", new WeatherServiceFunction(apiNinjasApiKey))
				.description("Get a current weather for a location")
				.inputType(WeatherRequest.class)
				.toolCallResultConverter((weatherResponse, returnType) -> {
          var schema = ModelOptionsUtils.getJsonSchema(WeatherResponse.class, false);
          var json = ModelOptionsUtils.toJsonString(weatherResponse);
          return schema + "\n" + json;
        })
				.build();

		var systemMessage = new SystemPromptTemplate("When talking about weather, you'll receive sunrise and sunset as epoch time in GMT. " +
			"Please determine the timezone where the city is located and provide the local time for sunrise and sunset in format HH:mm:ss. " +
			"You can also explicitly mention the local timezone for a city.").createMessage();
		var message = new PromptTemplate(question.question()).createMessage();
		var response = chatClient.prompt(new Prompt(List.of(systemMessage, message))).tools(toolCallback).call().content();
		return new Answer(response);
	}
}
