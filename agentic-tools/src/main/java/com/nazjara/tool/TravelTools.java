package com.nazjara.tool;

import com.nazjara.model.Flight;
import com.nazjara.model.Hotel;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * A deliberately large set of travel tools (15), so that sending all of them with every
 * request becomes noticeably expensive. That is the problem {@code ToolSearchToolCallingAdvisor}
 * solves.
 *
 * <p>Each {@link Tool} method becomes a tool definition: the method name, its
 * {@code description} and a JSON schema built from the parameters and their {@link ToolParam}
 * descriptions. The model only ever sees those definitions, so descriptions are what it
 * uses to pick a tool.
 *
 * <p>All data is <b>fake and deterministic</b> (derived from the inputs), so the module
 * needs no external APIs or keys. Only {@link #getLocalTime} returns real data.
 */
@Slf4j
@Component
public class TravelTools {

	private static final Map<String, BigDecimal> EUR_RATES = Map.of(
		"EUR", BigDecimal.ONE, "USD", new BigDecimal("1.09"), "GBP", new BigDecimal("0.85"),
		"UAH", new BigDecimal("45.20"), "PLN", new BigDecimal("4.30"), "JPY", new BigDecimal("162.50"),
		"CHF", new BigDecimal("0.95"));

	/**
	 * Fake forecast for a city and date.
	 *
	 * @param city city name
	 * @param date ISO date
	 * @return forecast summary
	 */
	@Tool(description = "Get the weather forecast for a city on a given date")
	public String getWeatherForecast(@ToolParam(description = "City name") String city,
			@ToolParam(description = "Date, ISO-8601 (yyyy-MM-dd)") String date) {
		var seed = seed(city, date);
		var conditions = List.of("sunny", "partly cloudy", "cloudy", "light rain", "showers").get(seed % 5);
		return "%s on %s: %s, %d°C".formatted(city, date, conditions, 8 + seed % 20);
	}

	/**
	 * Converts money using fixed demo exchange rates.
	 *
	 * @param amount amount to convert
	 * @param from source currency code
	 * @param to target currency code
	 * @return converted amount, or an error text for unknown currencies
	 */
	@Tool(description = "Convert an amount of money between currencies (ISO 4217 codes, e.g. EUR, USD, UAH)")
	public String convertCurrency(@ToolParam(description = "Amount to convert") BigDecimal amount,
			@ToolParam(description = "Source currency code") String from,
			@ToolParam(description = "Target currency code") String to) {
		var fromRate = EUR_RATES.get(from.toUpperCase(Locale.ROOT));
		var toRate = EUR_RATES.get(to.toUpperCase(Locale.ROOT));
		if (fromRate == null || toRate == null) {
			return "Unsupported currency. Supported: " + EUR_RATES.keySet();
		}
		var converted = amount.divide(fromRate, 6, RoundingMode.HALF_UP).multiply(toRate).setScale(2, RoundingMode.HALF_UP);
		return "%s %s = %s %s".formatted(amount, from, converted, to);
	}

	/**
	 * Real current local time for an IANA time zone.
	 *
	 * @param timeZone IANA zone id
	 * @return local date and time
	 */
	@Tool(description = "Get the current local date and time in a time zone")
	public String getLocalTime(@ToolParam(description = "IANA time zone id, e.g. Europe/Kyiv") String timeZone) {
		return ZonedDateTime.now(ZoneId.of(timeZone)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	/**
	 * Fake flight search.
	 *
	 * @param from departure city
	 * @param to arrival city
	 * @param date ISO date
	 * @return available flights
	 */
	@Tool(description = "Search available flights between two cities on a date")
	public List<Flight> searchFlights(@ToolParam(description = "Departure city") String from,
			@ToolParam(description = "Arrival city") String to,
			@ToolParam(description = "Date, ISO-8601 (yyyy-MM-dd)") String date) {
		var seed = seed(from, to, date);
		return List.of(
			new Flight("SA" + (100 + seed % 900), from, to, date, "07:15", "10:05", BigDecimal.valueOf(89 + seed % 120)),
			new Flight("SA" + (100 + (seed + 7) % 900), from, to, date, "18:40", "21:30", BigDecimal.valueOf(129 + seed % 90)));
	}

	/**
	 * Fake flight booking. The user id comes from {@link ToolContext}, which the application
	 * fills in; it is never shown to the model, so the model cannot book for someone else.
	 *
	 * @param flightNumber flight to book
	 * @param toolContext application-provided context
	 * @return booking confirmation
	 */
	@Tool(description = "Book a flight by its flight number for the current user")
	public String bookFlight(@ToolParam(description = "Flight number from searchFlights") String flightNumber, ToolContext toolContext) {
		var userId = toolContext.getContext().get("userId");
		log.info("Booking flight {} for user {}", flightNumber, userId);
		return "Booked %s for user %s, confirmation F-%d".formatted(flightNumber, userId, seed(flightNumber, String.valueOf(userId)));
	}

	/**
	 * Fake hotel search.
	 *
	 * @param city city name
	 * @param checkIn ISO date
	 * @param nights number of nights
	 * @return available hotels
	 */
	@Tool(description = "Search hotels in a city for a check-in date and number of nights")
	public List<Hotel> searchHotels(@ToolParam(description = "City name") String city,
			@ToolParam(description = "Check-in date, ISO-8601 (yyyy-MM-dd)") String checkIn,
			@ToolParam(description = "Number of nights") int nights) {
		var seed = seed(city, checkIn);
		return List.of(
			new Hotel("H" + seed % 1000, "Old Town Inn", city, 3, BigDecimal.valueOf(70 + seed % 40)),
			new Hotel("H" + (seed + 1) % 1000, "Grand " + city, city, 5, BigDecimal.valueOf(190 + seed % 80)));
	}

	/**
	 * Fake hotel booking, using the user id from {@link ToolContext}.
	 *
	 * @param hotelId hotel to book
	 * @param toolContext application-provided context
	 * @return booking confirmation
	 */
	@Tool(description = "Book a hotel by its id for the current user")
	public String bookHotel(@ToolParam(description = "Hotel id from searchHotels") String hotelId, ToolContext toolContext) {
		var userId = toolContext.getContext().get("userId");
		return "Booked hotel %s for user %s, confirmation H-%d".formatted(hotelId, userId, seed(hotelId, String.valueOf(userId)));
	}

	/**
	 * Fake list of attractions.
	 *
	 * @param city city name
	 * @return attractions
	 */
	@Tool(description = "List the top tourist attractions in a city")
	public List<String> getAttractions(@ToolParam(description = "City name") String city) {
		return List.of(city + " Old Town", city + " History Museum", city + " Central Park", city + " Opera House");
	}

	/**
	 * Fake visa rules.
	 *
	 * @param passportCountry traveller's passport country
	 * @param destinationCountry destination country
	 * @return visa requirement
	 */
	@Tool(description = "Check visa requirements for a passport holder travelling to a country")
	public String getVisaRequirements(@ToolParam(description = "Passport country") String passportCountry,
			@ToolParam(description = "Destination country") String destinationCountry) {
		return seed(passportCountry, destinationCountry) % 3 == 0
			? "Visa required for %s citizens travelling to %s.".formatted(passportCountry, destinationCountry)
			: "Visa-free for up to 90 days for %s citizens in %s.".formatted(passportCountry, destinationCountry);
	}

	/**
	 * Fake public holidays.
	 *
	 * @param country country name
	 * @param year year
	 * @return holidays
	 */
	@Tool(description = "List public holidays in a country for a year")
	public List<String> getPublicHolidays(@ToolParam(description = "Country name") String country,
			@ToolParam(description = "Year, e.g. 2026") int year) {
		return List.of(year + "-01-01 New Year's Day", year + "-05-01 Labour Day", year + "-12-25 Christmas Day");
	}

	/**
	 * Fake travel advisory.
	 *
	 * @param country country name
	 * @return advisory level
	 */
	@Tool(description = "Get the current travel safety advisory for a country")
	public String getTravelAdvisory(@ToolParam(description = "Country name") String country) {
		return country + ": " + List.of("exercise normal precautions", "exercise increased caution").get(seed(country) % 2);
	}

	/**
	 * Fake daily budget estimate.
	 *
	 * @param city city name
	 * @param style budget, mid-range or luxury
	 * @return estimated daily cost in EUR, excluding accommodation
	 */
	@Tool(description = "Estimate the daily spending in EUR (food, transport, sights; accommodation excluded)")
	public String estimateDailyBudget(@ToolParam(description = "City name") String city,
			@ToolParam(description = "Travel style: budget, mid-range or luxury") String style) {
		var base = switch (style.toLowerCase(Locale.ROOT)) {
			case "budget" -> 40;
			case "luxury" -> 250;
			default -> 90;
		};
		return "%s, %s: about %d EUR per day".formatted(city, style, base + seed(city) % 30);
	}

	/**
	 * Fake emergency numbers.
	 *
	 * @param country country name
	 * @return emergency numbers
	 */
	@Tool(description = "Get emergency phone numbers for a country")
	public String getEmergencyNumbers(@ToolParam(description = "Country name") String country) {
		return country + ": general emergency 112";
	}

	/**
	 * Fake restaurant recommendations.
	 *
	 * @param city city name
	 * @param cuisine preferred cuisine
	 * @return restaurants
	 */
	@Tool(description = "Recommend restaurants in a city for a cuisine")
	public List<String> getRestaurantRecommendations(@ToolParam(description = "City name") String city,
			@ToolParam(description = "Cuisine, e.g. local, italian, vegetarian") String cuisine) {
		return List.of("%s %s Kitchen".formatted(city, cuisine), "The %s Table".formatted(cuisine), "Bistro " + city);
	}

	/**
	 * Fake local transport info.
	 *
	 * @param city city name
	 * @return transport options and prices
	 */
	@Tool(description = "Describe public transport options and ticket prices in a city")
	public String getLocalTransport(@ToolParam(description = "City name") String city) {
		return "%s: tram and bus, single ticket %d.%02d EUR, day pass available".formatted(city, 1 + seed(city) % 3, seed(city) % 100);
	}

	private static int seed(String... values) {
		return Math.abs(String.join("|", values).toLowerCase(Locale.ROOT).hashCode());
	}
}
