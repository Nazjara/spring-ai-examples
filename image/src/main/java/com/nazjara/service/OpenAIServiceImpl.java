package com.nazjara.service;

import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageClient;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

	private final OpenAiImageClient imageClient;

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
	public String getDescription(MultipartFile file) {
		//TODO
		return null;
	}
}
