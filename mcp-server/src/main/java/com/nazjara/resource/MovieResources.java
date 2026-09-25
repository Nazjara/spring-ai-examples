package com.nazjara.resource;

import com.nazjara.model.Movie;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

/**
 * MCP <b>resources</b>: read-only data the client can fetch by URI, like files or GET
 * endpoints. Unlike tools, the model does not call resources; the client application
 * decides when to load them, typically to attach them to the conversation as context.
 *
 * <p>{@code movies://titles} is a fixed resource. {@code movies://{title}} is a resource
 * template: the {@code {title}} URI variable is bound to the method parameter of the same
 * name.
 */
@Component
public class MovieResources {

	private final JsonMapper jsonMapper;
	private final List<Movie> movies;

	public MovieResources(JsonMapper jsonMapper, @Value("classpath:movies.json") Resource moviesFile) throws IOException {
		this.jsonMapper = jsonMapper;
		try (InputStream inputStream = moviesFile.getInputStream()) {
			this.movies = jsonMapper.readValue(inputStream, new TypeReference<>() {});
		}
	}

	/**
	 * Lists the titles of all available movies.
	 *
	 * @return JSON array of titles
	 */
	@McpResource(uri = "movies://titles", name = "movie-titles", description = "Titles of all available movies", mimeType = "application/json")
	public String titles() {
		return jsonMapper.writeValueAsString(movies.stream().map(Movie::title).toList());
	}

	/**
	 * Returns details of one movie, matched case-insensitively by title.
	 *
	 * @param title movie title from the URI
	 * @return JSON object with the movie details, or a not-found message
	 */
	@McpResource(uri = "movies://{title}", name = "movie", description = "Details of a movie by title", mimeType = "application/json")
	public String movie(String title) {
		return movies.stream()
			.filter(movie -> movie.title().equalsIgnoreCase(title))
			.findFirst()
			.map(jsonMapper::writeValueAsString)
			.orElse("{\"error\": \"Movie not found: " + title.replace("\"", "") + "\"}");
	}
}
