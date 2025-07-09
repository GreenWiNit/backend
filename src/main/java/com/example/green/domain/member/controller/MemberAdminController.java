package com.example.green.domain.member.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.member.controller.docs.MemberAdminControllerDocs;
import com.example.green.domain.member.repository.dto.BasicInfoSearchCondition;
import com.example.green.domain.member.repository.dto.MemberPointsDto;
import com.example.green.domain.member.service.MemberQueryService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/members")
public class MemberAdminController implements MemberAdminControllerDocs {

	private final MemberQueryService memberQueryService;

	@GetMapping("/points")
	public ApiTemplate<PageTemplate<MemberPointsDto>> searchMembersPoint(
		@ModelAttribute @ParameterObject BasicInfoSearchCondition condition
	) {
		PageTemplate<MemberPointsDto> result = memberQueryService.searchMembersPoint(condition);
		return ApiTemplate.ok(() -> "사용자 포인트 페이지 조회 성공", result);
	}
}
