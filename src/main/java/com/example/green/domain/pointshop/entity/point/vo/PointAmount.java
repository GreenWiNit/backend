package com.example.green.domain.pointshop.entity.point.vo;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.green.domain.pointshop.exception.point.PointException;
import com.example.green.domain.pointshop.exception.point.PointExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointAmount {

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	private PointAmount(BigDecimal amount) {
		this.amount = amount.setScale(2, RoundingMode.DOWN);
	}

	public static PointAmount of(BigDecimal value) {
		validateNullData(value, "포인트 금액은 필수 값입니다.");
		if (value.compareTo(BigDecimal.ZERO) < 0) {
			throw new PointException(PointExceptionMessage.INVALID_POINT_AMOUNT);
		}
		return new PointAmount(value);
	}

	public PointAmount add(PointAmount amount) {
		return PointAmount.of(this.amount.add(amount.getAmount()));
	}

	public PointAmount subtract(PointAmount spendAmount) {
		return PointAmount.of(this.amount.subtract(spendAmount.getAmount()));
	}
}
