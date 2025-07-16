package com.example.green.domain.point.entity.vo;

import static com.example.green.domain.point.exception.PointExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.green.domain.point.exception.PointException;
import com.example.green.domain.point.exception.PointExceptionMessage;

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
		validateNullData(amount, REQUIRE_POINT_AMOUNT);
		this.amount = amount.setScale(2, RoundingMode.DOWN);
	}

	public static PointAmount of(BigDecimal value) {
		if (value.compareTo(BigDecimal.ZERO) < 0) {
			throw new PointException(PointExceptionMessage.INVALID_POINT_AMOUNT);
		}
		return new PointAmount(value);
	}

	public static PointAmount of(long value) {
		if (value < 0) {
			throw new PointException(PointExceptionMessage.INVALID_POINT_AMOUNT);
		}
		return new PointAmount(BigDecimal.valueOf(value));
	}

	public static PointAmount ofZero() {
		return new PointAmount(BigDecimal.ZERO);
	}

	public PointAmount add(PointAmount amount) {
		return PointAmount.of(this.amount.add(amount.getAmount()));
	}

	public PointAmount subtract(PointAmount spendAmount) {
		return PointAmount.of(this.amount.subtract(spendAmount.getAmount()));
	}

	public boolean canSpend(PointAmount spendAmount) {
		return this.amount.compareTo(spendAmount.getAmount()) >= 0;
	}
}
