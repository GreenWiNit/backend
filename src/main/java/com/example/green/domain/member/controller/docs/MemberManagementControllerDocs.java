package com.example.green.domain.member.controller.docs;

import com.example.green.domain.member.dto.admin.MemberListRequestDto;
import com.example.green.domain.member.dto.admin.MemberListResponseDto;
import com.example.green.domain.member.dto.admin.WithdrawnMemberListResponseDto;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Admin Member Management API", description = "관리자용 회원 관리 API")
public interface MemberManagementControllerDocs {

	@Operation(summary = "관리자용 활성 회원 목록 조회", description = "관리자가 활성 회원 목록을 페이징으로 조회합니다. 탈퇴한 회원은 제외됩니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "활성 회원 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiTemplate.class),
				examples = @ExampleObject(value = """
					{
						"success": true,
						"message": "회원 목록 조회가 완료되었습니다.",
						"result": {
							"totalElements": 25,
							"totalPages": 3,
							"currentPage": 0,
							"pageSize": 10,
							"hasNext": true,
							"content": [
								{
									"memberId": 1,
									"memberKey": "naver_123456789",
									"email": "user@naver.com",
									"nickname": "홍길동",
									"phoneNumber": "010-1234-5678",
									"joinDate": "2025-01-15T10:30:00",
									"role": "일반회원",
									"provider": "naver"
								}
							]
						}
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ExceptionResponse.class)
			)
		)
	})
	@ApiErrorStandard
	ApiTemplate<PageTemplate<MemberListResponseDto>> getMemberList(MemberListRequestDto request);

	@Operation(summary = "관리자용 활성 회원 목록 엑셀 다운로드", description = "관리자가 전체 활성 회원 목록을 엑셀 파일로 다운로드합니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "엑셀 다운로드 성공",
			content = @Content(
				mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				schema = @Schema(type = "string", format = "binary")
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	void downloadMemberListExcel(HttpServletResponse response);

	@Operation(summary = "관리자용 탈퇴 회원 목록 조회", description = "관리자가 탈퇴한 회원 목록을 페이징으로 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "탈퇴 회원 목록 조회 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ApiTemplate.class),
				examples = @ExampleObject(value = """
					{
						"success": true,
						"message": "탈퇴 회원 목록 조회가 완료되었습니다.",
						"result": {
							"totalElements": 5,
							"totalPages": 1,
							"currentPage": 0,
							"pageSize": 10,
							"hasNext": false,
							"content": [
								{
									"memberId": 1,
									"memberKey": "naver_123456789",
									"email": "user@naver.com",
									"nickname": "홍길동",
									"phoneNumber": "010-1234-5678",
									"joinDate": "2025-01-15T10:30:00",
									"withdrawalDate": "2025-01-20T14:20:00",
									"role": "일반회원",
									"provider": "naver"
								}
							]
						}
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	@ApiErrorStandard
	ApiTemplate<PageTemplate<WithdrawnMemberListResponseDto>> getWithdrawnMemberList(MemberListRequestDto request);

	@Operation(summary = "관리자용 탈퇴 회원 목록 엑셀 다운로드", description = "관리자가 전체 탈퇴 회원 목록을 엑셀 파일로 다운로드합니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "엑셀 다운로드 성공",
			content = @Content(
				mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				schema = @Schema(type = "string", format = "binary")
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	void downloadWithdrawnMemberListExcel(HttpServletResponse response);

	@Operation(summary = "관리자용 회원 강제 삭제", description = "관리자가 회원을 강제로 삭제(탈퇴 처리)합니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원 삭제 성공",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"success": true,
						"message": "회원 삭제가 완료되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		),
		@ApiResponse(
			responseCode = "404",
			description = "회원을 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	@ApiErrorStandard
	NoContent deleteMember(String memberKey);

	@Operation(summary = "사용자 별 포인트 조회 (ad_E01_001)", description = "사용자 별 포인트 조회를 합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 포인트 페이지 조회 성공"),
		@ApiResponse(
			responseCode = "403",
			description = "관리자 권한 필요",
			content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
		)
	})
	ApiTemplate<PageTemplate<MemberPointsDto>> searchMembersPoint(BasicInfoSearchCondition condition);
} 