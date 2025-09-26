package com.example.green.domain.common.idempotency;

import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.utils.ApiTemplateObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "idempotencies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class IdemPotency {

	@Id
	private String idempotencyKey;

	@Getter
	@Column(nullable = false)
	private String response;

	private IdemPotency(String idempotencyKey, ApiTemplate<?> response) {
		this.idempotencyKey = idempotencyKey;
		this.response = ApiTemplateObjectMapper.toString(response);
	}

	public static IdemPotency of(String idempotencyKey, Object response) {
		try {
			return new IdemPotency(idempotencyKey, (ApiTemplate<?>)response);
		} catch (ClassCastException e) {
			log.error("Idempotency Cast Exception: response = {}", response, e);
			throw new BusinessException(GlobalExceptionMessage.INTERNAL_SERVER_ERROR_MESSAGE);
		}
	}

	public ApiTemplate<?> toResponse() {
		return ApiTemplateObjectMapper.toApiTemplate(response);
	}
}
