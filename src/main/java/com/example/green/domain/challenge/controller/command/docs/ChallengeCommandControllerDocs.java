package com.example.green.domain.challenge.controller.command.docs;

import com.example.green.domain.challenge.controller.command.docs.annotation.ChallengeCreateDocs;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.global.api.ApiTemplate;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[챌린지-클라이언트] 챌린지 API", description = "개인 챌린지 조회, 참여, 탈퇴 API")
public interface ChallengeCommandControllerDocs {

	@ChallengeCreateDocs
	ApiTemplate<Long> create(ChallengeType type, AdminChallengeCreateDto dto);
}
