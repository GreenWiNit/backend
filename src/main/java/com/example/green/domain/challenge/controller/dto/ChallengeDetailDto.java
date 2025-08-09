package com.example.green.domain.challenge.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChallengeDetailDto(
	Long id,
	String title,
	LocalDateTime beginDateTime,
	LocalDateTime endDateTime,
	String imageUrl,
	BigDecimal point,
	boolean canParticipate
) {
}
