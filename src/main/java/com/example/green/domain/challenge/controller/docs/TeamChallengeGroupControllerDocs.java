package com.example.green.domain.challenge.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "팀 챌린지 그룹 관리", description = "팀 챌린지 그룹 생성, 조회, 수정, 삭제 및 참가 관리")
public interface TeamChallengeGroupControllerDocs {

	@Operation(
		summary = "팀 챌린지 그룹 목록 조회",
		description = "특정 팀 챌린지의 그룹 목록을 커서 기반 페이지네이션으로 조회합니다. (페이지 사이즈: 20개 고정)",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공")
	@ApiErrorStandard
	ApiTemplate<CursorTemplate<Long, TeamChallengeGroupListResponseDto>> getTeamChallengeGroups(
		@Parameter(description = "팀 챌린지 ID", required = true, example = "1")
		@PathVariable Long challengeId,
		@Parameter(description = "커서 (마지막으로 조회한 그룹 ID), 처음 조회시 생략")
		@RequestParam(required = false) Long cursor,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(
		summary = "팀 챌린지 그룹 생성",
		description = "새로운 팀 챌린지 그룹을 생성하고 생성자를 리더로 등록합니다.",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 생성 성공")
	@ApiError400
	@ApiErrorStandard
	ApiTemplate<Long> createTeamChallengeGroup(
		@Parameter(description = "팀 챌린지 ID", required = true, example = "1")
		@PathVariable Long challengeId,
		@Parameter(description = "그룹 생성 요청 정보", required = true)
		@Valid @RequestBody TeamChallengeGroupCreateRequestDto request,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(
		summary = "팀 챌린지 그룹 상세 조회",
		description = "특정 그룹의 상세 정보를 조회합니다.",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 상세 조회 성공")
	@ApiErrorStandard
	ApiTemplate<TeamChallengeGroupDetailResponseDto> getTeamChallengeGroupDetail(
		@Parameter(description = "그룹 ID", required = true, example = "1")
		@PathVariable Long groupId,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(
		summary = "팀 챌린지 그룹 참가",
		description = "특정 그룹에 참가합니다. 해당 팀 챌린지에 먼저 참가되어 있어야 합니다.",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 참가 성공")
	@ApiError400
	@ApiErrorStandard
	NoContent joinTeamChallengeGroup(
		@Parameter(description = "그룹 ID", required = true, example = "1")
		@PathVariable Long groupId,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(
		summary = "팀 챌린지 그룹 수정",
		description = "그룹 정보를 수정합니다. 리더만 수정할 수 있습니다.",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 수정 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음 (리더가 아님)")
	@ApiError400
	@ApiErrorStandard
	NoContent updateTeamChallengeGroup(
		@Parameter(description = "그룹 ID", required = true, example = "1")
		@PathVariable Long groupId,
		@Parameter(description = "그룹 수정 요청 정보", required = true)
		@Valid @RequestBody TeamChallengeGroupUpdateRequestDto request,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(
		summary = "팀 챌린지 그룹 삭제",
		description = "그룹을 삭제합니다. 리더만 삭제할 수 있습니다.",
		tags = {"팀 챌린지 그룹 관리"}
	)
	@ApiResponse(responseCode = "200", description = "그룹 삭제 성공")
	@ApiResponse(responseCode = "403", description = "권한 없음 (리더가 아님)")
	@ApiErrorStandard
	NoContent deleteTeamChallengeGroup(
		@Parameter(description = "그룹 ID", required = true, example = "1")
		@PathVariable Long groupId,
		@Parameter(hidden = true) @AuthenticationPrincipal PrincipalDetails principalDetails
	);
}
