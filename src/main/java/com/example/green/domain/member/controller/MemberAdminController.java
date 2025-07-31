package com.example.green.domain.member.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.member.controller.docs.MemberManagementControllerDocs;
import com.example.green.domain.member.controller.message.MemberResponseMessage;
import com.example.green.domain.member.dto.admin.MemberDeleteRequestDto;
import com.example.green.domain.member.dto.admin.MemberListRequestDto;
import com.example.green.domain.member.dto.admin.MemberListResponseDto;
import com.example.green.domain.member.dto.admin.WithdrawnMemberListResponseDto;
import com.example.green.domain.member.service.MemberAdminService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
@AdminApi(reason = "회원 관리는 관리자만 접근 가능")
public class MemberAdminController implements MemberManagementControllerDocs {

	private final MemberAdminService memberAdminService;
	private final ExcelDownloader excelDownloader;

	@Override
	@GetMapping
	public ApiTemplate<PageTemplate<MemberListResponseDto>> getMemberList(
		@ParameterObject @ModelAttribute @Valid MemberListRequestDto request
	) {
		log.info("[ADMIN] 회원 목록 조회 요청: page={}, size={}", request.page(), request.size());
		
		PageTemplate<MemberListResponseDto> result = memberAdminService.getMemberList(request);
		
		return ApiTemplate.ok(MemberResponseMessage.MEMBER_LIST_RETRIEVED, result);
	}

	@Override
	@GetMapping("/excel")
	public void downloadMemberListExcel(HttpServletResponse response) {
		log.info("[ADMIN] 회원 목록 엑셀 다운로드 요청");
		
		List<MemberListResponseDto> members = memberAdminService.getAllMembersForExcel();
		excelDownloader.downloadAsStream(members, response);
		
		log.info("[ADMIN] 회원 목록 엑셀 다운로드 완료: count={}", members.size());
	}

	@Override
	@GetMapping("/withdrawn")
	public ApiTemplate<PageTemplate<WithdrawnMemberListResponseDto>> getWithdrawnMemberList(
		@ParameterObject @ModelAttribute @Valid MemberListRequestDto request
	) {
		log.info("[ADMIN] 탈퇴 회원 목록 조회 요청: page={}, size={}", request.page(), request.size());
		
		PageTemplate<WithdrawnMemberListResponseDto> result = memberAdminService.getWithdrawnMemberList(request);
		
		return ApiTemplate.ok(MemberResponseMessage.WITHDRAWN_MEMBER_LIST_RETRIEVED , result);
	}

	@Override
	@GetMapping("/withdrawn/excel")
	public void downloadWithdrawnMemberListExcel(HttpServletResponse response) {
		log.info("[ADMIN] 탈퇴 회원 목록 엑셀 다운로드 요청");
		
		List<WithdrawnMemberListResponseDto> members = memberAdminService.getAllWithdrawnMembersForExcel();
		excelDownloader.downloadAsStream(members, response);
		
		log.info("[ADMIN] 탈퇴 회원 목록 엑셀 다운로드 완료: count={}", members.size());
	}

	@Override
	@PostMapping("/delete")
	public NoContent deleteMember(@RequestBody @Valid MemberDeleteRequestDto request) {
		log.info("[ADMIN] 회원 강제 삭제 요청: memberKey={}", request.memberKey());

		memberAdminService.validateMemberExistsByMemberKey(request.memberKey());

		memberAdminService.deleteMemberByMemberKey(request.memberKey());
		
		return NoContent.ok(MemberResponseMessage.MEMBER_DELETED);
	}
}
