package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;

/**
 * Retrieval Augmented Generation (RAG): answers questions using the documents
 * loaded into the vector store rather than only the model's training data.
 */
public interface AiService {
	/**
	 * Retrieves the document chunks most similar to the question and asks the model
	 * to answer using them.
	 *
	 * @param question the user's question
	 * @return an answer grounded in the retrieved documents
	 */
	Answer getAnswer(Question question);
}
