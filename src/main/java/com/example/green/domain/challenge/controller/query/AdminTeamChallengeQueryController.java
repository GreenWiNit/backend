package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengesDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminTeamChallengeQueryControllerDocs;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/team")
@RequiredArgsConstructor
public class AdminTeamChallengeQueryController implements AdminTeamChallengeQueryControllerDocs {

	private final TeamChallengeQuery teamChallengeQuery;
	private final ExcelDownloader excelDownloader;

	@GetMapping
	public ApiTemplate<PageTemplate<AdminTeamChallengesDto>> getTeamChallenges(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		PageTemplate<AdminTeamChallengesDto> result = teamChallengeQuery.findChallengePage(page, size);
		return ApiTemplate.ok(TEAM_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/excel")
	public void downloadTeamChallenges(HttpServletResponse response) {
		List<AdminTeamChallengesDto> result = teamChallengeQuery.findTeamChallengeForExcel();
		excelDownloader.downloadAsStream(result, response);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getTeamChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = teamChallengeQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/{challengeId}/participants")
	public ApiTemplate<CursorTemplate<Long, AdminChallengeParticipantListResponseDto>> getChallengeParticipants(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Long cursor) {
		// todo: 해야 댐
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_PARTICIPANTS_FOUND, null);
	}

	@GetMapping("/groups/{groupId}")
	public ApiTemplate<AdminTeamChallengeGroupDetailResponseDto> getGroupDetail(@PathVariable Long groupId) {
		//AdminTeamChallengeGroupDetailResponseDto result = adminChallengeService.getGroupDetail(groupId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_DETAIL_FOUND, null);
	}
}
