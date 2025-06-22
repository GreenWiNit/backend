package com.example.green.domain.file.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.green.domain.file.domain.vo.Purpose;

@Component
public class StringToPurposeConverter implements Converter<String, Purpose> {
	@Override
	public Purpose convert(String source) {
		for (Purpose purpose : Purpose.values()) {
			if (purpose.getValue().equals(source)) {
				return purpose;
			}
		}
		throw new IllegalArgumentException("Cannot convert '" + source + "' to Purpose");
	}
}
