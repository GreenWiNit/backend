package com.example.green.domain.pointshop.entity.point.vo;

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
	private String detail;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TargetType targetType;

	public static PointSource ofTarget(Long targetId, String detail, TargetType targetType) {
		validateAutoIncrementId(targetId, "포인트 출처의 Id 값은 필수 값 입니다.");
		validateEmptyString(detail, "포인트 출처의 상세 내용은 필수 값 입니다.");
		validateNullData(targetType, "포인트 출처의 타입 정보는 필수 값 입니다.");
		return new PointSource(targetId, detail, targetType);
	}

	public static PointSource ofEvent(String detail) {
		validateEmptyString(detail, "포인트 출처의 상세 내용은 필수 값 입니다.");
		return new PointSource(null, detail, TargetType.EVENT);
	}
}
