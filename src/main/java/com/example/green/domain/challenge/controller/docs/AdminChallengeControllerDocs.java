package com.example.green.domain.challenge.controller.docs;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeListResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "어드민 챌린지 관리 API", description = "어드민 챌린지 생성/수정/이미지/전시여부 등 관리 API")
public interface AdminChallengeControllerDocs {

	@Operation(
		summary = "챌린지 이미지 추가/수정",
		description = "챌린지의 이미지를 URL로 등록/수정합니다. (어드민 전용)"
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 이미지 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	@ApiResponse(responseCode = "404", description = "챌린지 없음",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminChallengeDetailResponseDto> updateChallengeImage(
		@Parameter(
			name = "challengeId",
			description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId,
		@RequestBody(description = "챌린지 이미지 URL", required = true, content = @Content(schema = @Schema(
			implementation = AdminChallengeImageUpdateRequestDto.class))) AdminChallengeImageUpdateRequestDto request);

	@Operation(summary = "챌린지 생성",
		description = """
			챌린지를 생성합니다. (이미지 URL 포함)
			
			**챌린지 타입 (challengeType):**
			- PERSONAL: 개인 챌린지
			- TEAM: 팀 챌린지
			
			**전시 상태 (displayStatus):**
			- VISIBLE: 전시 (사용자에게 보임)
			- HIDDEN: 숨김 (사용자에게 보이지 않음)
			""")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Long> createTeamChallenge(
		@RequestBody(description = "챌린지 생성 요청", required = true, content = @Content(schema =
		@Schema(implementation = AdminChallengeCreateRequestDto.class))) AdminChallengeCreateRequestDto request
	);

	@Operation(summary = "챌린지 수정 (사진x)",
		description = "챌린지를 수정합니다. (이미지 및 전시 상태 제외)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 수정 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Void> updateChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId,
		@RequestBody(description = "챌린지 수정 요청", required = true, content = @Content(schema = @Schema(
			implementation = AdminChallengeUpdateRequestDto.class))) AdminChallengeUpdateRequestDto request);

	@SuppressWarnings("checkstyle:RegexpSingleline")
	@Operation(summary = "챌린지 전시여부 수정", description = """
		챌린지의 전시 상태를 변경합니다.
		
		**전시 상태 (displayStatus):**
		- VISIBLE: 전시 (사용자에게 보임)
		- HIDDEN: 숨김 (사용자에게 보이지 않음)
		""")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "전시 상태 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Void> updateChallengeDisplayStatus(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId,
		@RequestBody(description = "전시 상태 변경 요청", required = true,
			content = @Content(schema = @Schema(implementation =
				AdminChallengeDisplayStatusUpdateRequestDto.class))) AdminChallengeDisplayStatusUpdateRequestDto request
	);

	@Operation(summary = "개인 챌린지 목록 조회", description = "개인 챌린지 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "개인 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<CursorTemplate<Long, AdminPersonalChallengeListResponseDto>> getPersonalChallenges(
		@Parameter(description = "커서 (마지막 챌린지 ID) - 첫 번째 조회 시에는 아무것도 넣지 말고 조회하세요") Long cursor
	);

	@Operation(summary = "팀 챌린지 목록 조회", description = "팀 챌린지 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<CursorTemplate<Long, AdminTeamChallengeListResponseDto>> getTeamChallenges(
		@Parameter(description = "커서 (마지막 챌린지 ID) - 첫 번째 조회 시에는 아무것도 넣지 말고 조회하세요") Long cursor
	);

	@Operation(summary = "챌린지 상세 조회", description = "챌린지 상세 정보를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminChallengeDetailResponseDto> getChallengeDetail(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId
	);

	@Operation(summary = "챌린지 참여자 목록 조회", description = "챌린지의 참여자 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "참여자 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<CursorTemplate<Long, AdminChallengeParticipantListResponseDto>> getChallengeParticipants(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "커서 (마지막 참여자 ID) - 첫 번째 조회 시에는 아무것도 넣지 말고 조회하세요") Long cursor
	);

	@Operation(summary = "그룹 목록 조회", description = "챌린지 그룹 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<CursorTemplate<Long, AdminTeamChallengeGroupListResponseDto>> getGroups(
		@Parameter(description = "커서 (마지막 그룹 ID) - 첫 번째 조회 시에는 아무것도 넣지 말고 조회하세요") Long cursor
	);

	@Operation(summary = "그룹 상세 조회", description = "챌린지 그룹 상세 정보를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "그룹 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminTeamChallengeGroupDetailResponseDto> getGroupDetail(
		@Parameter(name = "groupId", description = "그룹 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long groupId
	);
}
