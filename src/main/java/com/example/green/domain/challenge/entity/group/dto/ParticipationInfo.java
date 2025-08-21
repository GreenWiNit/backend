package com.example.green.domain.challenge.entity.group.dto;

public record ParticipationInfo(
	boolean participating,
	boolean certified,
	boolean isLeader
) {
}