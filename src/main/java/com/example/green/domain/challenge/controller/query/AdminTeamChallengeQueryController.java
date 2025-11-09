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
import com.example.green.domain.challenge.controller.query.docs.AdminTeamChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamChallengesDto;
import com.example.green.domain.challenge.repository.query.ChallengeAdminQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.AdminApi;
import com.example.green.infra.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/team")
@RequiredArgsConstructor
@AdminApi
public class AdminTeamChallengeQueryController implements AdminTeamChallengeQueryControllerDocs {

	private final ChallengeAdminQuery challengeAdminQuery;
	private final ExcelDownloader excelDownloader;

	@GetMapping
	public ApiTemplate<PageTemplate<AdminTeamChallengesDto>> getTeamChallenges(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		PageTemplate<AdminChallengesDto> temp = challengeAdminQuery.findChallengePage(page, size, TEAM);
		List<AdminTeamChallengesDto> data = temp.content().stream().map(AdminTeamChallengesDto::of).toList();
		PageTemplate<AdminTeamChallengesDto> result = convertTemp(temp, data);

		return ApiTemplate.ok(TEAM_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/excel")
	public void downloadTeamChallenges(HttpServletResponse response) {
		List<AdminChallengesDto> temp = challengeAdminQuery.findChallengePageExcel(TEAM);
		List<AdminTeamChallengesDto> result = temp.stream().map(AdminTeamChallengesDto::of).toList();
		excelDownloader.downloadAsStream(result, response);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getTeamChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = challengeAdminQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	private static PageTemplate<AdminTeamChallengesDto> convertTemp(
		PageTemplate<AdminChallengesDto> temp,
		List<AdminTeamChallengesDto> data
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
