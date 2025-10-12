package com.example.green.global.config.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@SuppressWarnings({"unchecked", "rawtypes"})
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return source -> {
			try {
				return convertUpperCase(targetType, source.trim());
			} catch (NullPointerException e) {
				return null;
			}
		};
	}

	private <T extends Enum> T convertUpperCase(Class<T> targetType, String source) {
		try {
			return (T)Enum.valueOf(targetType, source.toUpperCase());
		} catch (IllegalArgumentException e) {
			return convertFromKebabCase(targetType, source);
		}
	}

	private <T extends Enum> T convertFromKebabCase(Class<T> targetType, String source) {
		try {
			return (T)Enum.valueOf(targetType, source.replace("-", "_").toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("변환할 수 없는 요청: " + source);
		}
	}
}
