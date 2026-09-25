package com.nazjara;

import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for the prompt-engineering examples. Each subclass is a set of live
 * experiments against the chat model; read the prompts and run the tests to compare outputs.
 */
@SpringBootTest
public class BaseTestClass {

	@Autowired
	ChatClient.Builder chatClientBuilder;

	/**
	 * Sends a prompt as a single user message.
	 *
	 * @param prompt the prompt text
	 * @return the model's reply
	 */
	String chat(String prompt) {
		var promptTemplate = new PromptTemplate(prompt);
		var promptToSend = promptTemplate.create();
		return chatClientBuilder.build().prompt(promptToSend).call().content();
	}

	/**
	 * Creates a prompt template with its variables already bound.
	 *
	 * @param template StringTemplate text with {@code {placeholders}}
	 * @param variables values for the placeholders
	 * @return the bound template; call {@code create()} to render the prompt
	 */
	PromptTemplate template(String template, Map<String, ?> variables) {
		return PromptTemplate.builder().template(template).variables(Map.copyOf(variables)).build();
	}
}
