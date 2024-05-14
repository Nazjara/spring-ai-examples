package com.nazjara.bootstrap;

import com.nazjara.configuration.VectorStoreProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@Slf4j
@RequiredArgsConstructor
public class MilvusVectorStoreLoader implements CommandLineRunner
{
	private final VectorStore vectorStore;
	private final VectorStoreProperties vectorStoreProperties;

	@Override
	public void run(String... args) throws Exception {
		log.info("Loading Milvus vector store...");

		if (vectorStore.similaritySearch("Sportsman").isEmpty())
		{
			log.info("Loading documents into vector store...");

			vectorStoreProperties.getDocumentsToLoad().forEach(document -> {
				log.debug("Loading document: {}", document.getFilename());
				var documentReader = new TikaDocumentReader(document);
				var docs = documentReader.get();
				var textSplitter = new TokenTextSplitter();
				var splitDocs = textSplitter.apply(docs);
				vectorStore.add(splitDocs);
			});
		}

		log.info("Loading done");
	}
}
