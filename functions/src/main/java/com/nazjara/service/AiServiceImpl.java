package com.nazjara.service;

import com.nazjara.function.WeatherServiceFunction;
import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import com.nazjara.model.WeatherRequest;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@link AiService} that exposes {@link WeatherServiceFunction} to the model as a tool.
 *
 * <p>Flow of one request:
 * <ol>
 *   <li>The request carries the tool's name, description and input JSON schema
 *       (derived from {@link WeatherRequest}).</li>
 *   <li>The model replies with a tool call instead of text, e.g.
 *       {@code currentWeather({"city":"Lviv","country":"Ukraine"})}.</li>
 *   <li>Spring AI's {@code ToolCallingAdvisor} (auto-registered in {@link ChatClient}
 *       since 2.0) runs the function and sends its JSON result back to the model.</li>
 *   <li>The model writes the final answer using that result.</li>
 * </ol>
 */
@Service
public class AiServiceImpl implements AiService {

	private final ChatClient chatClient;
	private final ToolCallback weatherToolCallback;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, @Value("${api-ninjas.api-key}") String apiNinjasApiKey) {
		this.chatClient = chatClientBuilder.build();
		this.weatherToolCallback = FunctionToolCallback
				.builder("currentWeather", new WeatherServiceFunction(apiNinjasApiKey))
				.description("Get a current weather for a location")
				.inputType(WeatherRequest.class)
				.build();
	}

	@Override
	public Answer getAnswer(Question question) {
		var systemMessage = new SystemPromptTemplate("When talking about weather, you'll receive sunrise and sunset as epoch time in GMT. " +
			"Please determine the timezone where the city is located and provide the local time for sunrise and sunset in format HH:mm:ss. " +
			"You can also explicitly mention the local timezone for a city.").createMessage();
		var message = new PromptTemplate(question.question()).createMessage();
		var response = chatClient.prompt(new Prompt(List.of(systemMessage, message))).tools(weatherToolCallback).call().content();
		return new Answer(response);
	}
}
