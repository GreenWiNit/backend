package com.example.green.global.utils;

import static com.example.green.global.error.exception.GlobalExceptionMessage.*;

import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiTemplateObjectMapper {

	private ApiTemplateObjectMapper() {
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static ApiTemplate<?> toApiTemplate(String responseString) {
		try {
			return objectMapper.readValue(responseString, ApiTemplate.class);
		} catch (JsonProcessingException e) {
			log.error("string to response parsing error: {}", e.toString());
			throw new BusinessException(INTERNAL_SERVER_ERROR_MESSAGE);
		}
	}

	public static String toString(Object response) {
		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			log.error("response to string parsing error: {}", e.toString());
			throw new BusinessException(INTERNAL_SERVER_ERROR_MESSAGE);
		}
	}
}