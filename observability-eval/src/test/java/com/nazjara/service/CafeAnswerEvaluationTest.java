package com.nazjara.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.nazjara.model.Question;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * LLM-as-a-judge tests. Model answers vary between runs, so exact-string assertions don't
 * work. Instead a second, stronger model grades each answer:
 * <ul>
 *   <li>{@link RelevancyEvaluator}: does the answer address the question, given the
 *       retrieved context?</li>
 *   <li>{@link FactCheckingEvaluator}: is every claim in the answer supported by the
 *       context? This catches hallucinations.</li>
 * </ul>
 * The app runs on Haiku and the judge on Sonnet; a judge weaker than the model under test
 * gives unreliable verdicts. The negative test checks the judge itself: a fabricated answer
 * must fail.
 *
 * <p>Runs only when {@code ANTHROPIC_API_KEY} is set (live calls, small cost).
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class CafeAnswerEvaluationTest {

	@Autowired
	AiService aiService;

	@Autowired
	ChatModel chatModel;

	ChatClient.Builder judge;

	@BeforeEach
	void setUp() {
		judge = ChatClient.builder(chatModel).defaultOptions(AnthropicChatOptions.builder().model("claude-sonnet-5"));
	}

	@Test
	void answerIsRelevantToTheQuestion() {
		var question = "When are you open on Saturday?";
		var response = aiService.ask(new Question(question));

		var evaluation = RelevancyEvaluator.builder().chatClientBuilder(judge).build()
			.evaluate(new EvaluationRequest(question, documents(response.sources()), response.answer()));

		assertThat(evaluation.isPass()).as("answer: %s", response.answer()).isTrue();
	}

	@Test
	void answerIsGroundedInTheKnowledgeBase() {
		var response = aiService.ask(new Question("Can I bring my dog, and how much is a flat white?"));

		var evaluation = FactCheckingEvaluator.builder(judge).build()
			.evaluate(new EvaluationRequest(documents(response.sources()), response.answer()));

		assertThat(evaluation.isPass()).as("answer: %s", response.answer()).isTrue();
	}

	@Test
	void fabricatedAnswerFailsFactCheck() {
		var response = aiService.ask(new Question("When are you open on Sunday?"));
		var fabricated = "Spring Brew Café is open 24 hours a day on Sundays and accepts Bitcoin.";

		var evaluation = FactCheckingEvaluator.builder(judge).build()
			.evaluate(new EvaluationRequest(documents(response.sources()), fabricated));

		assertThat(evaluation.isPass()).isFalse();
	}

	private static List<Document> documents(List<String> sources) {
		return sources.stream().map(Document::new).toList();
	}
}
