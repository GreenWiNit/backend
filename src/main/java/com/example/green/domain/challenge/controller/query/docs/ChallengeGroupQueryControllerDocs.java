package com.example.green.domain.challenge.controller.query.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.dto.MyChallengeGroupDto;
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

	@Operation(summary = "챌린지 별 나의 팀(그룹) 목록 조회 - B01_006 (나의 팀)", description = "팀 목록을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "내 팀 목록을 조회했습니다.")
	ApiTemplate<CursorTemplate<String, MyChallengeGroupDto>> getMyTeamChallengeGroups(
		@Parameter(description = "챌린지 ID", in = PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "커서 (시간,ID) - 생략 시 첫 조회") String cursor,
		@Parameter(description = "페이지 사이즈 (생략 가능)", example = "20") Integer size,
		PrincipalDetails principalDetails);

	@Operation(summary = "그룹 상세 조회 - B01_006, B01_007 (팀 정보)", description = "그룹을 상세 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "내 팀 목록을 조회했습니다.")
	ApiTemplate<ChallengeGroupDetailDto> getTeamChallengeGroupDetail(
		@Parameter(description = "그룹 ID", in = PATH, required = true, example = "1")
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	);

	@Operation(summary = "챌린지 별 팀(그룹) 목록 조회 - B01_007 (팀 선택하기)", description = "팀 목록을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 목록을 조회했습니다.")
	ApiTemplate<CursorTemplate<String, ChallengeGroupDto>> getTeamChallengeGroups(
		@Parameter(description = "챌린지 ID", in = PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "커서 (시간,ID) - 생략 시 첫 조회") String cursor,
		@Parameter(description = "페이지 사이즈 (생략 가능)", example = "20") Integer size,
		PrincipalDetails principalDetails);
}
