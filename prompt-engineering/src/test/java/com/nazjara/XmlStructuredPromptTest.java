package com.nazjara;

import org.junit.jupiter.api.Test;

/**
 * XML tags separate instructions from data, so text inside a document is never mistaken for
 * an instruction, and prompts can refer to parts by name. With long inputs, put the documents
 * first and the task last: answers get measurably better when the question follows the data.
 */
class XmlStructuredPromptTest extends BaseTestClass {

	@Test
	void documentsFirstTaskLast() {
		var answer = chatClient.prompt()
			.user("""
					%s

					<instructions>
					Write the description for the book's product page from the reviews above. \
					Shoppers skim, so keep it to a short paragraph, and cover what all three reviews agree on.
					</instructions>""".formatted(Reviews.asXml(Reviews.BOOK)))
			.call()
			.content();

		System.out.println(answer);
	}

	@Test
	void quotesGroundTheAnswer() {
		var answer = chatClient.prompt()
			.user("""
					%s

					<instructions>
					What do these reviewers criticize about the product? First copy the sentences that \
					support each criticism into <quotes> tags, citing the review index, then give your \
					answer in <answer> tags, based only on those quotes.
					</instructions>""".formatted(Reviews.asXml(Reviews.TUMBLER)))
			.call()
			.content();

		System.out.println(answer);
	}
}
