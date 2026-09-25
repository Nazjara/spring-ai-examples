package com.nazjara.service;

import com.nazjara.model.AnswerWithSources;
import com.nazjara.model.Question;
import com.nazjara.tool.CafeTools;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

/**
 * {@link AiService} using {@link QuestionAnswerAdvisor}, the one-line version of the manual
 * RAG steps in the {@code rag} module. Before each call the advisor embeds the question,
 * retrieves the closest chunks and appends them to the user message. It also stores them in
 * the response context under {@link QuestionAnswerAdvisor#RETRIEVED_DOCUMENTS}, which is how
 * this class returns the sources that the evaluation tests grade the answer against.
 *
 * <p><b>Observability</b> needs no code here. Spring AI instruments {@link ChatClient}, the
 * advisors, the chat model, embeddings, the vector store and tool calls with Micrometer
 * Observations. With {@code spring-boot-starter-opentelemetry}, each request becomes a trace
 * (verified in Tempo). Advisors nest in their order, and the RAG advisor sits <i>inside</i>
 * the tool loop:
 * <pre>
 * http post /ask
 * └─ spring_ai chat_client
 *    └─ tool_calling                     ToolCallingAdvisor: the tool loop
 *       └─ question_answer               QuestionAnswerAdvisor: RAG
 *          ├─ embedding                  question → vector (local ONNX)
 *          └─ call
 *             └─ chat claude-haiku-...   first model call: asks for the tool
 *    ... execute_tool getTodaysSpecial, then a second chat span for the final answer
 * </pre>
 * {@code SimpleVectorStore} has no span of its own; its search shows up as the
 * {@code embedding} span.
 *
 * The {@code spring.ai.*.observations.log-*} properties add prompt and completion text to
 * the spans. That's handy for learning, but it's user data, so keep it off in production.
 */
@Service
public class AiServiceImpl implements AiService {

	private final ChatClient chatClient;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, CafeTools cafeTools) {
		this.chatClient = chatClientBuilder
			.defaultSystem("You are the Spring Brew Café assistant. Answer briefly, using only the provided context and tools. If the answer is not there, say you don't know.")
			.defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
				.searchRequest(SearchRequest.builder().topK(3).build())
				.build())
			.defaultTools(cafeTools)
			.build();
	}

	@Override
	public AnswerWithSources ask(Question question) {
		var response = chatClient.prompt().user(question.question()).call().chatClientResponse();

		@SuppressWarnings("unchecked")
		var documents = (List<Document>) response.context().getOrDefault(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS, List.of());
		var answer = response.chatResponse().getResult().getOutput().getText();
		return new AnswerWithSources(answer, documents.stream().map(Document::getText).toList());
	}
}
