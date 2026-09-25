package com.nazjara.tool;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Live data not contained in the static knowledge base, so the model has a reason to call a
 * tool, and the trace shows an {@code execute_tool} span next to the chat span.
 */
@Component
public class CafeTools {

	private static final Map<DayOfWeek, String> SPECIALS = Map.of(
		DayOfWeek.MONDAY, "borscht with pampushky", DayOfWeek.TUESDAY, "mushroom banosh",
		DayOfWeek.WEDNESDAY, "potato varenyky", DayOfWeek.THURSDAY, "cheese syrnyky",
		DayOfWeek.FRIDAY, "apple strudel", DayOfWeek.SATURDAY, "Lviv cheesecake",
		DayOfWeek.SUNDAY, "honey cake");

	/**
	 * Today's special in the café's time zone (fake data, keyed by day of week).
	 *
	 * @return today's special dish
	 */
	@Tool(description = "Get today's special dish at Spring Brew Café")
	public String getTodaysSpecial() {
		var today = LocalDate.now(ZoneId.of("Europe/Kyiv")).getDayOfWeek();
		return "Today's special (" + today + ") is " + SPECIALS.get(today) + ".";
	}
}
