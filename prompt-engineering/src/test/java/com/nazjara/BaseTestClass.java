package com.nazjara;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaseTestClass {

	@Autowired
	ChatClient.Builder chatClientBuilder;

	String chat(String prompt) {
		var promptTemplate = new PromptTemplate(prompt);
		var promptToSend = promptTemplate.create();
		return chatClientBuilder.build().prompt(promptToSend).call().content();
	}
}
