package com.example.green.domain.info.controller;

import com.example.green.domain.info.dto.InfoRequest;
import com.example.green.domain.info.dto.admin.InfoDetailResponseByAdmin;
import com.example.green.domain.info.dto.admin.InfoSearchListResponseByAdmin;
import com.example.green.domain.info.dto.user.InfoDetailResponseByUser;
import com.example.green.domain.info.dto.user.InfoSearchListResponseByUser;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.DetailedExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "정보공유 API", description = "정보공유 생성, 조회, 수정, 삭제 API")
public interface InfoControllerDocs {
	@Operation(summary = "관리자 전체 Info 페이지 단위로 조회", description = "관리자가 페이징 정보를 기준으로 정보공유 목록을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "관리자 정보공유 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<InfoSearchListResponseByAdmin> getInfosForAdmin(
		@Parameter(name = "page", description = "조회할 페이지 번호 (0부터 시작)", example = "0", required = true) Integer page,
		@Parameter(name = "size", description = "페이지당 게시글 수 (20개로)", example = "20", required = true) Integer size
	);

	@Operation(summary = "관리자 Info 상세 페이지 조회", description = "관리자가 정보공유 ID로 상세 페이지를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "관리자 게시글 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<InfoDetailResponseByAdmin> getInfoDetailForAdmin(
		@Parameter(name = "infoId", description = "조회할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "P000001") String infoId
	);

	@Operation(summary = "관리자 Info 등록", description = "관리자가 정보공유 게시글을 등록합니다.")
	@ApiErrorStandard
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "관리자 게시글 등록 성공",
			useReturnTypeSchema = true
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 – 유효성 검증 실패 (에러는 배열로 반환 - 가장 처음 것이 우선순위)",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = DetailedExceptionResponse.class),
				examples = @ExampleObject(
					name = "ValidationError",
					summary = "요청 유효성 검증 실패 예시",
					value = """
						{
						  "success": false,
						  "message": "요청 데이터에 유효성 검증 오류가 있습니다.",
						  "errors": [
							{
							  "field": "title",
							  "message": "제목을 입력해주세요."
							},
							{
							  "field": "title",
							  "message": "제목은 최소 1자 이상, 최대 30자 이하까지 등록할 수 있습니다."
							},
							{
							  "field": "content",
							  "message": "내용을 입력해주세요."
							},
							{
							  "field": "content",
							  "message": "내용은 최소 10자 이상, 최대 1000자 이하까지 등록할 수 있습니다."
							},
							{
							  "field": "infoCategory",
							  "message": "카테고리가 선택되지 않았습니다."
							},
							{
							  "field": "imageUrl",
							  "message": "이미지가 첨부되지 않았습니다."
							},
							{
							  "field": "isDisplay",
							  "reason": "전시여부를 선택해주세요."
							}
						  ]
						}
						"""
				)
			)
		),
	})
	ApiTemplate<InfoDetailResponseByAdmin> saveInfo(InfoRequest saveRequest);

	@Operation(summary = "관리자 Info 수정", description = "관리자가 정보공유 ID로 게시글을 수정합니다.")
	@ApiErrorStandard
	@ApiError400
	@ApiResponse(responseCode = "200", description = "관리자 게시글 수정 성공", useReturnTypeSchema = true)
	ApiTemplate<InfoDetailResponseByAdmin> updateInfo(
		@Parameter(name = "infoId", description = "수정할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "P000001") String infoId,
		InfoRequest updateRequest
	);

	@Operation(summary = "관리자 Info 삭제", description = "관리자가 정보공유 ID로 게시글을 삭제합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "관리자 게시글 삭제 성공", useReturnTypeSchema = true)
	NoContent deleteInfo(
		@Parameter(name = "infoId", description = "삭제할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "P000001") String infoId
	);

	@Operation(summary = "일반 사용자 Info 전체 조회 (페이징 없음)", description = "일반 사용자가 페이징 없이 정보공유 목록을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "일반 사용자 페이징 없는 정보공유 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<InfoSearchListResponseByUser> getInfosForUser();

	@Operation(summary = "로그인한 사용자 Info 상세 페이지 조회", description = "로그인한 사용자가 정보공유 ID로 상세 페이지를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "관리자 게시글 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<InfoDetailResponseByUser> getInfoDetailForUser(
		@Parameter(name = "infoId", description = "조회할 게시글의 ID",
			in = ParameterIn.PATH, required = true, example = "P000001") String infoId
	);

}
