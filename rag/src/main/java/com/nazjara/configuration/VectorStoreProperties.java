package com.nazjara.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * RAG settings bound from {@code ai.rag.*}: where the local vector store file lives
 * and which documents (classpath or URL) to ingest.
 */
@Configuration
@ConfigurationProperties(prefix = "ai.rag")
@Getter
@Setter
public class VectorStoreProperties {

	private String vectorStorePath;
	private List<Resource> documentsToLoad;
}
