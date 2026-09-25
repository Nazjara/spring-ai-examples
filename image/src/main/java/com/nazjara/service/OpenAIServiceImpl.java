package com.nazjara.service;

import com.nazjara.model.Question;
import java.io.IOException;
import java.util.Base64;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class OpenAIServiceImpl implements OpenAIService {

  private final ChatClient chatClient;
  private final OpenAiImageModel imageClient;

  public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder, OpenAiImageModel imageClient) {
    this.chatClient = chatClientBuilder.build();
    this.imageClient = imageClient;
  }

  @Override
  public byte[] getImage(Question question) {
    var options = OpenAiImageOptions.builder()
        .height(1024)
        .width(1024)
        .model("dall-e-3")
        .responseFormat("b64_json")
        .quality("hd")
        .style("natural")
        .build();

    var imagePrompt = new ImagePrompt(question.question(), options);
    var response = imageClient.call(imagePrompt);
    return Base64.getDecoder().decode(response.getResult().getOutput().getB64Json());
  }

  @Override
  public String getDescription(MultipartFile file) throws IOException {
    var media = Media.builder()
        .mimeType(MimeTypeUtils.parseMimeType(file.getContentType()))
        .data(new InputStreamResource(file.getInputStream()))
        .build();

    var userMessage = UserMessage.builder()
        .text("Explain what do you see on this picture?")
        .media(media)
        .build();

    return chatClient.prompt().messages(userMessage).call().content();
  }
}
