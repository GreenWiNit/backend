package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;

import com.example.green.domain.challenge.controller.dto.GroupCreateDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;

import jakarta.validation.Valid;

@Service
public class GroupService {

	public Long create(Long challengeId, Long memberId, GroupCreateDto dto) {
		return null;
	}

	public void update(Long groupId, @Valid TeamChallengeGroupUpdateRequestDto request, Long memberId) {
	}

	public void delete(Long groupId, Long memberId) {
	}

	public void join(Long groupId, Long memberId) {

	}
}
