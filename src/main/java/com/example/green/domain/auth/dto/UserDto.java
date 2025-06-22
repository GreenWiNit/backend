package com.example.green.domain.auth.dto;

public record UserDto(
	String role,
	String name,
	String username
) {
}
