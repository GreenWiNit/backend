package com.example.green.domain.pointshop.item.dto.response;

import java.math.BigDecimal;

public record UserPointCalculation(
	BigDecimal enablePoint,
	BigDecimal decreasePoint,
	BigDecimal remainPoint
) {
}
