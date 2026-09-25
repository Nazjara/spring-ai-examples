package com.nazjara.function;

import com.nazjara.model.WeatherRequest;
import com.nazjara.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

/**
 * HTTP client for the API Ninjas weather API, shaped as a {@link Function} so it can be
 * registered as a model tool.
 *
 * <p>The model never calls the API directly: it asks Spring AI to invoke this function with
 * a {@link WeatherRequest}, and the returned {@link WeatherResponse} is sent back to it
 * as JSON.
 */
@Slf4j
public class WeatherServiceFunction implements Function<WeatherRequest, WeatherResponse> {

	public static final String WEATHER_URL = "https://api.api-ninjas.com/v1/weather";

	private final RestClient restClient;

	public WeatherServiceFunction(String apiNinjasKey) {
		this.restClient = RestClient.builder()
			.baseUrl(WEATHER_URL)
			.defaultHeaders(httpHeaders -> {
				httpHeaders.set("X-Api-Key", apiNinjasKey);
				httpHeaders.set("Accept", "application/json");
				httpHeaders.set("Content-Type", "application/json");
			}).build();
	}

	/**
	 * Fetches the current weather for a city.
	 *
	 * @param weatherRequest city and country chosen by the model
	 * @return current weather as returned by API Ninjas
	 */
	@Override
	public WeatherResponse apply(WeatherRequest weatherRequest) {
		return restClient.get().uri(uriBuilder -> {
			log.info("Building URI for weather request: {}", weatherRequest);
			uriBuilder.queryParam("city", weatherRequest.city());
			uriBuilder.queryParam("country", weatherRequest.country());
			return uriBuilder.build();
		}).retrieve().body(WeatherResponse.class);
	}
}
