package com.example.green.domain.challengecert.controller.docs;

import java.util.List;

import com.example.green.domain.challenge.entity.certification.CertificationStatus;
import com.example.green.domain.challengecert.dto.AdminCertificationStatusUpdateRequestDto;
import com.example.green.domain.challengecert.dto.AdminChallengeTitleResponseDto;
import com.example.green.domain.challengecert.dto.AdminGroupCodeResponseDto;
import com.example.green.domain.challengecert.dto.AdminParticipantMemberKeyResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.docs.ApiErrorStandard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "관리자 챌린지 인증 API", description = "관리자용 챌린지 인증 관리 API")
public interface AdminChallengeCertificationControllerDocs {

	@Operation(
		summary = "1-1. 개인 챌린지 제목 목록 조회 (드롭다운용)",
		description = """
			관리자가 개인 챌린지의 제목 목록을 조회합니다.
			
			**연관 API 사용 순서:**
			1-1. **이 API** → 드롭다운에서 챌린지 선택용
			1-2. 참여자 memberKey 목록 조회 → 특정 챌린지 선택 시 참여자 드롭다운용  
			1. 개인 챌린지 인증 목록 복합 조건 조회 → 최종 목록 조회
			"""
	)
	@ApiResponse(responseCode = "200", description = "개인 챌린지 제목 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<List<AdminChallengeTitleResponseDto>> getPersonalChallengeTitles();

	@Operation(
		summary = "2. 팀 챌린지 인증 목록 복합 조건 조회 (메인 조회)",
		description = """
			관리자가 팀 챌린지 인증 목록을 복합 조건으로 조회합니다.
			
			**주요 기능:**
			- 챌린지 ID, 그룹 코드, 인증 상태 등으로 필터링 가능
			- 모든 조건은 선택사항 (null이면 전체 조회)
			- 커서 기반 페이지네이션 적용 (기본 10건)
			
			**사용 방법:**
			1. **전체 조회**: 모든 파라미터 null
			2. **특정 챌린지**: challengeId만 지정
			3. **특정 그룹**: groupCode만 지정 (2-2 API에서 조회한 값 사용)
			4. **특정 상태**: statuses 배열 지정 (PENDING, PAID, REJECTED)
			5. **조합 조건**: 여러 파라미터 동시 사용 가능
			
			**연관 API 활용:**
			- challengeId: "2-1. 팀 챌린지 제목 목록 조회"에서 선택한 값
			- groupCode: "2-2. 팀 챌린지 그룹 코드 목록 조회"에서 선택한 값
			
			**페이징:**
			- 첫 페이지: cursor 없이 호출
			- 다음 페이지: 응답의 nextCursor 값을 cursor 파라미터로 사용
			"""
	)
	@ApiResponse(responseCode = "200", description = "팀 챌린지 인증 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getTeamCertificationsWithFilters(
		@Parameter(name = "challengeId", description = "챌린지 ID (선택사항, null이면 전체 조회) - '2-1. 팀 챌린지 제목 목록 조회'에서 선택한 값",
			in = ParameterIn.QUERY, example = "1") Long challengeId,
		@Parameter(name = "groupCode", description = "그룹 코드 (선택사항, null이면 전체 그룹) - '2-2. 팀 챌린지 그룹 코드 목록 조회'에서 선택한 값",
			in = ParameterIn.QUERY, example = "T-20250109-143523-C8NQ") String groupCode,
		@Parameter(name = "statuses", description = "인증 상태 리스트 (선택사항, null이면 전체 상태) - PENDING(대기중), PAID(지급완료), REJECTED(거절됨)",
			in = ParameterIn.QUERY, example = "PENDING,PAID") List<CertificationStatus> statuses,
		@Parameter(name = "cursor", description = "커서 (페이징용, 마지막 인증 ID) - 첫 페이지는 null, 다음 페이지는 응답의 nextCursor 사용",
			in = ParameterIn.QUERY) Long cursor
	);

	@Operation(
		summary = "2-1. 팀 챌린지 제목 목록 조회 (드롭다운용)",
		description = """
			관리자가 팀 챌린지의 제목 목록을 조회합니다.
			
			**연관 API 사용 순서:**
			2-1. **이 API** → 드롭다운에서 팀 챌린지 선택용
			2-2. 팀 챌린지 그룹 코드 목록 조회 → 특정 팀 챌린지 선택 시 그룹 드롭다운용  
			2. 팀 챌린지 인증 목록 복합 조건 조회 → 최종 목록 조회
			"""
	)
	@ApiResponse(responseCode = "200", description = "팀 챌린지 제목 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<List<AdminChallengeTitleResponseDto>> getTeamChallengeTitles();

	@Operation(
		summary = "1-2. 개인 챌린지 참여자 memberKey 목록 조회 (드롭다운용)",
		description = """
			관리자가 특정 개인 챌린지의 참여자 memberKey 목록을 조회합니다.
			
			**사용 시점:** 
			- 1-1단계에서 특정 챌린지를 선택했을 때 호출
			- 참여자 드롭다운을 채우기 위한 용도
			
			**다음 단계:** 
			1. 개인 챌린지 인증 목록 복합 조건 조회에서 이 memberKey 값을 사용
			"""
	)
	@ApiResponse(responseCode = "200", description = "참여자 memberKey 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<List<AdminParticipantMemberKeyResponseDto>> getPersonalChallengeParticipantMemberKeys(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId
	);

	@Operation(
		summary = "1. 개인 챌린지 인증 목록 복합 조건 조회 (메인 조회)",
		description = """
			관리자가 개인 챌린지 인증 목록을 복합 조건으로 조회합니다.
			
			**주요 기능:**
			- 챌린지 ID, 참여자 memberKey, 인증 상태 등으로 필터링 가능
			- 모든 조건은 선택사항 (null이면 전체 조회)
			- 커서 기반 페이지네이션 적용 (기본 10건)
			
			**사용 방법:**
			1. **전체 조회**: 모든 파라미터 null
			2. **특정 챌린지**: challengeId만 지정
			3. **특정 참여자**: memberKey만 지정 (1-1 API에서 조회한 값 사용)
			4. **특정 상태**: statuses 배열 지정 (PENDING, PAID, REJECTED)
			5. **조합 조건**: 여러 파라미터 동시 사용 가능
			
			**연관 API 활용:**
			- challengeId: "1-1. 개인 챌린지 제목 목록 조회"에서 선택한 값
			- memberKey: "1-2. 참여자 memberKey 목록 조회"에서 선택한 값
			
			**페이징:**
			- 첫 페이지: cursor 없이 호출
			- 다음 페이지: 응답의 nextCursor 값을 cursor 파라미터로 사용
			"""
	)
	@ApiResponse(responseCode = "200", description = "개인 챌린지 인증 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getPersonalCertificationsWithFilters(
		@Parameter(name = "challengeId", description = "챌린지 ID (선택사항, null이면 전체 조회) - '1-1. 개인 챌린지 제목 목록 조회'에서 선택한 값",
			in = ParameterIn.QUERY, example = "1") Long challengeId,
		@Parameter(name = "memberKey", description = "참여자 memberKey (선택사항, null이면 전체 참여자) - '1-2. 참여자 memberKey 목록 조회'에서 선택한 값",
			in = ParameterIn.QUERY, example = "google_3421") String memberKey,
		@Parameter(name = "statuses", description = "인증 상태 리스트 (선택사항, null이면 전체 상태) - PENDING(대기중), PAID(지급완료), REJECTED(거절됨)",
			in = ParameterIn.QUERY, example = "PENDING,PAID") List<CertificationStatus> statuses,
		@Parameter(name = "cursor", description = "커서 (페이징용, 마지막 인증 ID) - 첫 페이지는 null, 다음 페이지는 응답의 nextCursor 사용",
			in = ParameterIn.QUERY) Long cursor
	);

	@Operation(
		summary = "2-2. 팀 챌린지 그룹 코드 목록 조회 (드롭다운용)",
		description = """
			관리자가 특정 팀 챌린지의 그룹 코드 목록을 조회합니다.
			
			**사용 시점:** 
			- 2-1단계에서 특정 팀 챌린지를 선택했을 때 호출
			- 그룹 드롭다운을 채우기 위한 용도
			
			**다음 단계:** 
			2. 팀 챌린지 인증 목록 복합 조건 조회에서 이 groupCode 값을 사용
			"""
	)
	@ApiResponse(responseCode = "200", description = "그룹 코드 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<List<AdminGroupCodeResponseDto>> getTeamChallengeGroupCodes(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId
	);

	@Operation(
		summary = "3. 인증 상태 업데이트 (승인/거절)",
		description = """
			관리자가 특정 인증의 상태를 승인/거절로 변경합니다.
			
			**사용 시점:**
			- 1단계(개인) 또는 2단계(팀) 조회 결과에서 특정 인증을 선택하여 상태 변경
			
			**가능한 상태 변경:**
			- PENDING → PAID (승인/지급완료)
			- PENDING → REJECTED (거절/거절됨)
			
			**제약사항:**
			- 이미 처리된 인증(PAID, REJECTED)은 상태 변경 불가
			- 개인/팀 챌린지 인증 모두 동일한 API 사용
			"""
	)
	@ApiResponse(responseCode = "200", description = "인증 상태 업데이트 성공", useReturnTypeSchema = true)
	@ApiError400
	@ApiErrorStandard
	ApiTemplate<Void> updateCertificationStatus(
		@Parameter(name = "certificationId", description = "인증 ID - 인증 목록 조회에서 얻은 ID 값",
			in = ParameterIn.PATH, required = true, example = "1") Long certificationId,
		@Parameter(description = "인증 상태 업데이트 요청 - PAID(승인/지급완료) 또는 REJECTED(거절/거절됨)", required = true)
		@Valid AdminCertificationStatusUpdateRequestDto request
	);
}
