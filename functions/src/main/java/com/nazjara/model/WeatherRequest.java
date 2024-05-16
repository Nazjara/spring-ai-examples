package com.nazjara.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonClassDescription("Weather API request")
public record WeatherRequest(@JsonProperty(required = true) @JsonPropertyDescription("The city go check whether in") String city,
                             @JsonProperty(required = true) @JsonPropertyDescription("The country where the city is located") String country) {
}
