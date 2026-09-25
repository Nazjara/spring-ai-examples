package com.nazjara.function;

import com.nazjara.model.WeatherRequest;
import com.nazjara.model.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

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
