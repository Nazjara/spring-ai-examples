package com.nazjara.service;

import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

	private final OpenAiImageClient imageClient;
	final ChatClient chatClient;

	@Override
	public byte[] getImage(Question question) {
		var options = OpenAiImageOptions.builder()
			.withHeight(1024)
			.withWidth(1024)
			.withModel("dall-e-3")
			.withResponseFormat("b64_json")
			.withQuality("hd")
			.withStyle("natural")
			.build();

		var imagePrompt = new ImagePrompt(question.question(), options);
		var response = imageClient.call(imagePrompt);
		return Base64.getDecoder().decode(response.getResult().getOutput().getB64Json());
	}

	@Override
	public String getDescription(MultipartFile file) throws IOException {
		var chatOptions = OpenAiChatOptions.builder()
			.withModel(OpenAiApi.ChatModel.GPT_4_O.getValue())
			.build();

		var userMessage = new UserMessage("Explain what do you see on this picture?",
			List.of(new Media(MediaType.IMAGE_JPEG, new InputStreamResource(file.getInputStream()))));

		return chatClient.call(new Prompt(List.of(userMessage), chatOptions)).getResult().getOutput().toString();
	}
}
