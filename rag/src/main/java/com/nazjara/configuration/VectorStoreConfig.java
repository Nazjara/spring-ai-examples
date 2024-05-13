package com.nazjara.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@Slf4j
public class VectorStoreConfig {

	@Bean
	SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient, VectorStoreProperties vectorStoreProperties) {
		var store =  new SimpleVectorStore(embeddingClient);
		var vectorStoreFile = new File(vectorStoreProperties.getVectorStorePath());

		if (vectorStoreFile.exists()) {
			store.load(vectorStoreFile);
		} else {
			log.debug("Loading documents into vector store");
			vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
				log.debug("Loading document: {}", document.getFilename());
				var documentReader = new TikaDocumentReader(document);
				var docs = documentReader.get();
				var textSplitter = new TokenTextSplitter();
				var splitDocs = textSplitter.apply(docs);
				store.add(splitDocs);
			});

			store.save(vectorStoreFile);
		}

		return store;
	}
}
