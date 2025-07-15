package com.example.green.domain.auth.dto;

public record TokenResponseDto(
	String accessToken,
	String memberKey,
	String userName
) {
}
