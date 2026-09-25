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

/**
 * {@link AiService} implementing RAG by hand, so each step is visible:
 * <ol>
 *   <li>{@link VectorStore#similaritySearch} embeds the question (local ONNX model) and
 *       returns the 4 closest document chunks.</li>
 *   <li>The chunks are pasted into {@code template/rag-prompt-template.st} together with
 *       the question.</li>
 *   <li>The system message ({@code template/system-message.st}) tells the model to answer
 *       only from those chunks.</li>
 * </ol>
 * Spring AI also ships {@code QuestionAnswerAdvisor}, which does the same in one line;
 * this class keeps the steps explicit for learning.
 */
@Service
public class AiServiceImpl implements AiService {

  private final ChatClient chatClient;
  private final VectorStore vectorStore;

  public AiServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
    this.chatClient = chatClientBuilder.build();
    this.vectorStore = vectorStore;
  }

  @Value("classpath:/template/rag-prompt-template.st")
  private Resource ragPromptTemplate;

  @Value("classpath:/template/rag-prompt-template-detailed.st")
  private Resource ragPromptTemplateDetailed;

  @Value("classpath:/template/system-message.st")
  private Resource systemMessageTemplate;

  @Override
  public Answer getAnswer(Question question) {
    var systemPromptTemplate = new SystemPromptTemplate(systemMessageTemplate);
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
