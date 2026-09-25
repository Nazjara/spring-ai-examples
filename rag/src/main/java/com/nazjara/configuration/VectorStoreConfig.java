package com.nazjara.configuration;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Default (non-{@code prod}) vector store: an in-memory {@link SimpleVectorStore}
 * persisted to a JSON file.
 *
 * <p>Under the {@code prod} profile this config is inactive and Milvus is used instead
 * (see {@code application-prod.properties} and {@code MilvusVectorStoreLoader}).
 */
@Configuration
@Slf4j
@Profile("!prod")
public class VectorStoreConfig {

	/**
	 * Creates the vector store, loading it from disk if the file exists, otherwise
	 * ingesting every configured document:
	 * <ol>
	 *   <li>{@link TikaDocumentReader} extracts text from any format (txt, PDF, HTML, ...).</li>
	 *   <li>{@link TokenTextSplitter} cuts it into chunks small enough to embed.</li>
	 *   <li>{@code vectorStore.add(...)} embeds each chunk with the {@link EmbeddingModel}
	 *       and stores the vectors.</li>
	 * </ol>
	 * Delete the file to force re-ingestion; changing the embedding model requires it.
	 *
	 * @param embeddingModel turns text into vectors (local ONNX all-MiniLM-L6-v2, 384 dims)
	 * @param vectorStoreProperties documents to load and the file path
	 * @return the populated vector store
	 */
	@Bean
	VectorStore simpleVectorStore(EmbeddingModel embeddingModel, VectorStoreProperties vectorStoreProperties) {
		var vectorStore = SimpleVectorStore.builder(embeddingModel).build();
		var vectorStoreFile = new File(vectorStoreProperties.getVectorStorePath());

		if (vectorStoreFile.exists()) {
			vectorStore.load(vectorStoreFile);
		} else {
			log.debug("Loading documents into vector store");
			vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
				log.debug("Loading document: {}", document.getFilename());
				var documentReader = new TikaDocumentReader(document);
				var docs = documentReader.get();
				var textSplitter = new TokenTextSplitter();
				var splitDocs = textSplitter.apply(docs);
				vectorStore.add(splitDocs);
			});

			vectorStore.save(vectorStoreFile);
		}

		return vectorStore;
	}
}
