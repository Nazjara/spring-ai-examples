package com.nazjara.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Builds the in-memory knowledge base used for RAG.
 */
@Configuration
public class KnowledgeBaseConfig {

	/**
	 * Reads {@code knowledge/cafe.md}, splits it into small chunks and embeds them with the
	 * local ONNX model. Small chunks keep each retrieved source focused, which makes the
	 * fact-checking evaluation more meaningful.
	 *
	 * <p>Embedding calls are observed too: each one shows up as an {@code embedding} span in
	 * the startup trace.
	 *
	 * @param embeddingModel local all-MiniLM-L6-v2 model
	 * @param knowledge the knowledge base document
	 * @return the populated vector store
	 */
	@Bean
	VectorStore vectorStore(EmbeddingModel embeddingModel, @Value("classpath:knowledge/cafe.md") Resource knowledge) {
		var vectorStore = SimpleVectorStore.builder(embeddingModel).build();
		var splitter = TokenTextSplitter.builder().withChunkSize(80).withMinChunkSizeChars(20).build();
		vectorStore.add(splitter.apply(new TextReader(knowledge).get()));
		return vectorStore;
	}
}
