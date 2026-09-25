package com.nazjara.client;

import com.nazjara.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * HTTP client for the API Ninjas weather API.
 *
 * <p>Plain Spring code with no MCP or AI dependency: {@code WeatherTools} is what turns it
 * into something a model can call.
 */
@Component
public class WeatherClient {

	private static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather";

	private final RestClient restClient;

	public WeatherClient(@Value("${api-ninjas.api-key}") String apiNinjasKey) {
		this.restClient = RestClient.builder()
			.baseUrl(WEATHER_URL)
			.defaultHeader("X-Api-Key", apiNinjasKey)
			.build();
	}

	/**
	 * Fetches the current weather for a city.
	 *
	 * @param city city name
	 * @param country country name
	 * @return current weather as returned by API Ninjas
	 */
	public WeatherResponse currentWeather(String city, String country) {
		return restClient.get()
			.uri(uriBuilder -> uriBuilder.queryParam("city", city).queryParam("country", country).build())
			.retrieve()
			.body(WeatherResponse.class);
	}
}
