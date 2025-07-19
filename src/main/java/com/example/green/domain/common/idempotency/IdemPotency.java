package com.example.green.domain.common.idempotency;

import com.example.green.global.api.ApiTemplate;
import com.example.green.global.utils.ApiTemplateObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "idempotencies")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	public static IdemPotency of(String idempotencyKey, ApiTemplate<?> response) {
		return new IdemPotency(idempotencyKey, response);
	}

	public ApiTemplate<?> toResponse() {
		return ApiTemplateObjectMapper.toApiTemplate(response);
	}
}
