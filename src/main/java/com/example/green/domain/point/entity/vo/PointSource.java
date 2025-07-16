package com.example.green.domain.point.entity.vo;

import static com.example.green.domain.point.exception.PointExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class PointSource {

	private Long targetId;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TargetType targetType;

	public static PointSource ofTarget(Long targetId, String description, TargetType targetType) {
		validateAutoIncrementId(targetId, REQUIRE_POINT_SOURCE_ID);
		validateEmptyString(description, REQUIRE_POINT_SOURCE_DESCRIPTION);
		validateNullData(targetType, REQUIRE_POINT_SOURCE_TYPE);
		return new PointSource(targetId, description, targetType);
	}

	public static PointSource ofEvent(String description) {
		validateEmptyString(description, REQUIRE_POINT_SOURCE_DESCRIPTION);
		return new PointSource(null, description, TargetType.EVENT);
	}
}
