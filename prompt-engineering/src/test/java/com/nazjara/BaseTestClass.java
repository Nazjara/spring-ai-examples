package com.nazjara;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaseTestClass {

	@Autowired
	OpenAiChatClient openAiChatClient;

	String chat(String prompt) {
		PromptTemplate promptTemplate = new PromptTemplate(prompt);
		Prompt promptToSend = promptTemplate.create();

		return openAiChatClient.call(promptToSend).getResult().getOutput().getContent();
	}

}
