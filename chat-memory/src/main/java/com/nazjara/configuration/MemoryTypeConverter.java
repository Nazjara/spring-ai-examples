package com.nazjara.configuration;

import com.nazjara.model.MemoryType;
import java.util.Locale;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MemoryTypeConverter implements Converter<String, MemoryType> {

	@Override
	public MemoryType convert(String source) {
		return MemoryType.valueOf(source.toUpperCase(Locale.ROOT));
	}
}
