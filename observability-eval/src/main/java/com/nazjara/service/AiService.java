package com.nazjara.service;

import com.nazjara.model.AnswerWithSources;
import com.nazjara.model.Question;

/**
 * Café assistant combining RAG and a tool. It's small on purpose: the goal is to watch it
 * (tracing) and grade it (evaluation), not the assistant itself.
 */
public interface AiService {

	/**
	 * Answers a guest question from the knowledge base and live tools.
	 *
	 * @param question the guest's question
	 * @return the answer and the knowledge-base chunks it was grounded on
	 */
	AnswerWithSources ask(Question question);
}
