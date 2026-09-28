package com.nazjara;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for the prompt-engineering examples. Each subclass is a set of live experiments
 * against the chat model: read the prompts, run the tests and inspect the printed output.
 */
@SpringBootTest
abstract class BaseTestClass {

	ChatClient chatClient;

	@BeforeEach
	void setUpChatClient(@Autowired ChatClient.Builder chatClientBuilder) {
		chatClient = chatClientBuilder.build();
	}
}
