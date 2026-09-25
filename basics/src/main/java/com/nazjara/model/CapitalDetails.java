package com.nazjara.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record CapitalDetails(
	@JsonPropertyDescription("The country name") String country,
	@JsonPropertyDescription("The capital city name") String city,
	@JsonPropertyDescription("The city population") long population,
	@JsonPropertyDescription("The region of the country the city is located in") String region,
	@JsonPropertyDescription("The primary language spoken in the city") String language,
	@JsonPropertyDescription("The currency used, as an ISO 4217 code") String currency) {
}
