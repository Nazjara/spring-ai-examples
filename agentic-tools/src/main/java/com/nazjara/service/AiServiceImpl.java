package com.nazjara.service;

import com.nazjara.model.AskResponse;
import com.nazjara.model.Question;
import com.nazjara.model.TripPlan;
import com.nazjara.tool.TravelTools;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.client.advisor.toolsearch.ToolSearchToolCallingAdvisor;
import org.springframework.ai.tool.toolsearch.index.lucene.LuceneToolIndex;
import org.springframework.stereotype.Service;

/**
 * {@link AiService} with two {@link ChatClient}s over the same {@link TravelTools}.
 *
 * <p><b>How the tool loop works in 2.0.</b> {@code ChatClient} auto-registers a
 * {@code ToolCallingAdvisor}. When the model answers with tool calls instead of text, the
 * advisor executes them, appends the results to the conversation and calls the model
 * again, repeating until the model returns a normal answer. This loop is what makes the
 * app "agentic": the model decides which steps to take, and your code only runs them.
 *
 * <ul>
 *   <li><b>{@code allTools}</b>: every request carries the definitions of all 15 tools.
 *       That's simple, but those definitions are paid for as input tokens on every
 *       iteration of the loop.</li>
 *   <li><b>{@code toolSearch}</b>: {@link ToolSearchToolCallingAdvisor} replaces the
 *       default tool-calling advisor. It indexes the tools in Lucene once per session and
 *       gives the model a single search tool instead. The model searches ("flights",
 *       "currency"), and only the matching definitions are added to later requests. The
 *       session is identified by the {@value #SESSION_ID_KEY} advisor parameter.</li>
 * </ul>
 *
 * <p><b>Guard rails</b> ({@code application.properties}): {@code spring.ai.tools.limits.*}
 * caps calls per tool (5) and per request (20). When a limit is hit, the model receives
 * an error result instead of the call running, so a confused model cannot loop forever.
 *
 * <p>{@link SimpleLoggerAdvisor} logs each request sent to the model, so you can see the
 * tool definitions shrink when tool search is enabled.
 */
@Service
public class AiServiceImpl implements AiService {

	private static final String SESSION_ID_KEY = "toolSearchSessionId";
	private static final String SYSTEM_PROMPT = "You are a travel assistant. Use the tools to look up facts; never invent flights, hotels or prices.";

	private final ChatClient allTools;
	private final ChatClient toolSearch;

	public AiServiceImpl(ChatClient.Builder chatClientBuilder, TravelTools travelTools) {
		this.allTools = chatClientBuilder.clone()
			.defaultSystem(SYSTEM_PROMPT)
			.defaultTools(travelTools)
			.defaultAdvisors(new SimpleLoggerAdvisor())
			.build();
		this.toolSearch = chatClientBuilder.clone()
			.defaultSystem(SYSTEM_PROMPT)
			.defaultTools(travelTools)
			.defaultAdvisors(
				ToolSearchToolCallingAdvisor.builder()
					.toolIndex(new LuceneToolIndex())
					.sessionIdKeyName(SESSION_ID_KEY)
					.maxResults(5)
					.build(),
				new SimpleLoggerAdvisor())
			.build();
	}

	@Override
	public AskResponse ask(Question question, String userId, boolean useToolSearch) {
		var chatClient = useToolSearch ? toolSearch : allTools;
		var response = chatClient.prompt()
			.user(question.question())
			.toolContext(Map.of("userId", userId))
			.advisors(a -> a.param(SESSION_ID_KEY, UUID.randomUUID().toString()))
			.call()
			.chatResponse();

		var usage = response.getMetadata().getUsage();
		return new AskResponse(response.getResult().getOutput().getText(), useToolSearch,
			usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>{@code .entity(TripPlan.class)} asks for JSON matching the schema, but a model can
	 * still return JSON that doesn't match: a missing field, or a string where a number
	 * belongs. {@link StructuredOutputValidationAdvisor} validates the reply against the
	 * schema and, on failure, re-asks the model with the validation errors, up to 3 times,
	 * before the result reaches the converter.
	 */
	@Override
	public TripPlan plan(Question question) {
		return allTools.prompt()
			.user(question.question())
			.toolContext(Map.of("userId", "planner"))
			.advisors(StructuredOutputValidationAdvisor.builder()
				.outputType(TripPlan.class)
				.maxRepeatAttempts(3)
				.build())
			.call()
			.entity(TripPlan.class);
	}
}
