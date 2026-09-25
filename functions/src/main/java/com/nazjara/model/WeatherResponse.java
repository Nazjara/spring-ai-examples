package com.nazjara.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

public record WeatherResponse(@JsonAlias("wind_speed") @JsonPropertyDescription("WindSpeed in KMH") BigDecimal windSpeed,
                              @JsonAlias("wind_degrees") @JsonPropertyDescription("Direction of wind") Integer windDegrees,
                              @JsonPropertyDescription("Current Temperature in Celsius") Integer temp,
                              @JsonPropertyDescription("Current Humidity") Integer humidity,
                              @JsonPropertyDescription("Epoch time of sunset GMT") Integer sunset,
                              @JsonPropertyDescription("Epoch time of Sunrise GMT") Integer sunrise,
                              @JsonAlias("min_temp") @JsonPropertyDescription("Low Temperature in Celsius") Integer minTemp,
                              @JsonAlias("cloud_pct") @JsonPropertyDescription("Cloud Coverage Percentage") Integer cloudPct,
                              @JsonAlias("feels_like") @JsonPropertyDescription("Temperature in Celsius") Integer feelsLike,
                              @JsonAlias("max_temp") @JsonPropertyDescription("MaximumTemperature in Celsius") Integer maxTemp) {
}
