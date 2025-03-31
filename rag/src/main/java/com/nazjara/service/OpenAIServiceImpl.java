package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;
import java.util.List;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class OpenAIServiceImpl implements OpenAIService {

  private final ChatClient chatClient;
  private final VectorStore vectorStore;

  public OpenAIServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
    this.chatClient = chatClientBuilder.build();
    this.vectorStore = vectorStore;
  }

  @Value("classpath:/template/rag-prompt-template.st")
  private Resource ragPromptTemplate;

  @Value("classpath:/template/rag-prompt-template-detailed.st")
  private Resource ragPromptTemplateDetailed;

  @Value("classpath:/template/system-message.st")
  private Resource systemMessage;

  @Override
  public Answer getAnswer(Question question) {
    var systemPromptTemplate = new SystemPromptTemplate(systemMessage);
    var systemMessage = systemPromptTemplate.createMessage();

    var documents = vectorStore.similaritySearch(
        SearchRequest.builder().query(question.question()).topK(4).build());
    var contentList = documents.stream().map(Document::getFormattedContent).toList();
    var promptTemplate = new PromptTemplate(ragPromptTemplate);
    var userMessage = promptTemplate.createMessage(Map.of("input", question.question(),
        "documents", String.join("\n", contentList)));

    var response = chatClient.prompt(new Prompt(List.of(systemMessage, userMessage))).call();
    return new Answer(response.content());
  }
}
