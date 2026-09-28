package com.nazjara;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Output format belongs in a schema, not in prompt text. {@code entity(...)} derives a JSON
 * schema from the record; {@code useProviderStructuredOutput()} sends it as Anthropic's native
 * {@code output_config.format}, so the response is constrained to the schema at decoding time
 * instead of the format being described in the prompt and parsed on a best-effort basis.
 */
class StructuredOutputTest extends BaseTestClass {

	enum Sentiment { POSITIVE, NEUTRAL, MIXED, NEGATIVE }

	record ReviewAnalysis(int index, Sentiment sentiment, List<String> emotions, boolean angry,
			String summary) {
	}

	record ReviewAnalyses(List<ReviewAnalysis> reviews) {
	}

	record Topics(List<String> topics) {
	}

	@Test
	void classifyReviews() {
		var result = chatClient.prompt()
			.user("""
					%s

					Analyze each review: its sentiment, the emotions the writer expresses, whether the \
					writer is angry, and a one-sentence summary in English.""".formatted(Reviews.asXml(Reviews.TUMBLER)))
			.call()
			.entity(ReviewAnalyses.class, spec -> spec.useProviderStructuredOutput());

		result.reviews().forEach(System.out::println);
		assertThat(result.reviews()).hasSize(Reviews.TUMBLER.size());
	}

	@Test
	void extractTopics() {
		var result = chatClient.prompt()
			.user("""
					<article>
					In a recent survey conducted by the government, public sector employees were asked to \
					rate their level of satisfaction with the department they work at. NASA was the most \
					popular department with a satisfaction rating of 95%%. The Social Security Administration \
					had the lowest rating, with only 45%% of employees satisfied. The government has pledged \
					to address the concerns raised and work towards improving job satisfaction.
					</article>

					List the topics this article discusses, one or two words each.""")
			.call()
			.entity(Topics.class, spec -> spec.useProviderStructuredOutput());

		System.out.println(result.topics());
		assertThat(result.topics()).isNotEmpty();
	}
}
