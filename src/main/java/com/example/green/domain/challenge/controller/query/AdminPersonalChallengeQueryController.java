package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage.*;
import static com.example.green.domain.challenge.entity.challenge.vo.ChallengeType.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminPersonalChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.repository.query.ChallengeAdminQuery;
import com.example.green.domain.challenge.util.MemberKeyConverter;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.PublicApi;
import com.example.green.infra.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/personal")
@RequiredArgsConstructor
@PublicApi
public class AdminPersonalChallengeQueryController implements AdminPersonalChallengeQueryControllerDocs {

	private final ChallengeAdminQuery challengeAdminQuery;
	private final ExcelDownloader excelDownloader;
	private final MemberKeyConverter converter;

	@GetMapping
	public ApiTemplate<PageTemplate<AdminPersonalChallengesDto>> getPersonalChallenges(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminChallengesDto> temp = challengeAdminQuery.findChallengePage(page, size, PERSONAL);
		List<AdminPersonalChallengesDto> data = temp.content().stream().map(AdminPersonalChallengesDto::of).toList();
		PageTemplate<AdminPersonalChallengesDto> result = convertTemp(temp, data);
		return ApiTemplate.ok(PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getPersonalChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = challengeAdminQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/excel")
	public void downloadChallengeExcel(HttpServletResponse response) {
		List<AdminChallengesDto> temp = challengeAdminQuery.findChallengePageExcel(PERSONAL);
		List<AdminPersonalChallengesDto> data = temp.stream().map(AdminPersonalChallengesDto::of).toList();
		excelDownloader.downloadAsStream(data, response);
	}

	@GetMapping("/{challengeId}/participants")
	public ApiTemplate<PageTemplate<AdminPersonalParticipationDto>> getChallengeParticipant(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminPersonalParticipationDto> result =
			challengeAdminQuery.findParticipantByChallenge(challengeId, page, size);
		converter.convertPage(result);
		return ApiTemplate.ok(CHALLENGE_PARTICIPANTS_FOUND, result);
	}

	@GetMapping("/{challengeId}/participants/excel")
	public void downloadParticipantExcel(@PathVariable Long challengeId, HttpServletResponse response) {
		List<AdminPersonalParticipationDto> result =
			challengeAdminQuery.findParticipantExcelByChallenge(challengeId);

		converter.convert(result);
		excelDownloader.downloadAsStream(result, response);
	}

	private static PageTemplate<AdminPersonalChallengesDto> convertTemp(
		PageTemplate<AdminChallengesDto> temp,
		List<AdminPersonalChallengesDto> data
	) {
		return new PageTemplate<>(
			temp.totalElements(),
			temp.totalPages(),
			temp.currentPage(),
			temp.pageSize(),
			temp.hasNext(),
			data);
	}
}
