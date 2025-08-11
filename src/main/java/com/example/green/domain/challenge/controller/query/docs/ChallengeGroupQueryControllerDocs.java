package com.example.green.domain.challenge.controller.query.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[챌린지-클라이언트] 팀 챌린지 그룹 API", description = "팀 챌린지 그룹 생성, 조회, 수정, 삭제 및 참가 관리")
public interface ChallengeGroupQueryControllerDocs {

	@Operation(summary = "챌린지 별 나의 팀(그룹) 목록 조회", description = "팀 목록을 조회합니다. (20개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "내 팀 목록을 조회했습니다.", useReturnTypeSchema = true)
	ApiTemplate<CursorTemplate<String, ChallengeGroupDto>> getTeamChallengeGroups(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId,
		@Parameter(description = "커서 (시간,ID) - 생략 시 첫 조회") String cursor,
		@Parameter(description = "페이지 사이즈 (생략 가능)", example = "20") Integer size,
		PrincipalDetails principalDetails);
}
