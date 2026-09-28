package com.nazjara;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

/**
 * Examples are the strongest signal in a prompt: the model copies their length, tone and shape.
 * Use them for what instructions describe poorly (a house style, an exact output shape), not for
 * tasks the model already does well. Pass them as user/assistant message pairs so they read as
 * prior turns, and vary them so the model learns the pattern rather than one sample.
 */
class FewShotTest extends BaseTestClass {

	private static final List<Message> HOUSE_STYLE_EXAMPLES = List.of(
			new UserMessage("Feature: double-wall vacuum insulation keeps drinks cold for 24 hours"),
			new AssistantMessage("Ice at breakfast. Ice at bedtime."),
			new UserMessage("Feature: laptop battery lasts 20 hours on a single charge"),
			new AssistantMessage("Leave the charger. Take the long flight."),
			new UserMessage("Feature: hiking boots are waterproof and weigh 400 grams per pair"),
			new AssistantMessage("Puddles lose. Your legs win."));

	@Test
	void examplesPinTheHouseStyle() {
		var answer = chatClient.prompt()
			.system("You write product taglines in the brand's house style.")
			.messages(HOUSE_STYLE_EXAMPLES)
			.user("Feature: noise-cancelling headphones block 95% of ambient sound")
			.call()
			.content();

		System.out.println(answer);
	}
}
