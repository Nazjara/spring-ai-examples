package com.nazjara.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.math.BigDecimal;
import java.util.List;

public record TripPlan(
	@JsonPropertyDescription("Destination city") String destination,
	@JsonPropertyDescription("Trip start date, ISO-8601 (yyyy-MM-dd)") String startDate,
	@JsonPropertyDescription("One entry per day of the trip") List<DayPlan> days,
	@JsonPropertyDescription("Items to pack, based on the forecast") List<String> packingList,
	@JsonPropertyDescription("Estimated total cost in EUR, flights and hotel included") BigDecimal estimatedBudgetEur) {

	public record DayPlan(
		@JsonPropertyDescription("Day number, starting at 1") int day,
		@JsonPropertyDescription("Weather summary for the day") String weather,
		@JsonPropertyDescription("Planned activities") List<String> activities) {
	}
}
