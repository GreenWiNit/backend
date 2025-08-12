package com.example.green.domain.challenge.controller.query;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminChallengeGroupQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.util.ClientHelper;
import com.example.green.domain.challenge.util.MemberKeyConverter;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
@AdminApi
public class AdminChallengeGroupQueryController implements AdminChallengeGroupQueryControllerDocs {

	private final ClientHelper clientHelper;
	private final MemberKeyConverter converter;
	private final ChallengeGroupQuery challengeGroupQuery;
	private final ExcelDownloader excelDownloader;

	@GetMapping("/groups")
	public ApiTemplate<PageTemplate<AdminChallengeGroupDto>> getGroups(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminChallengeGroupDto> result = challengeGroupQuery.findGroupPaging(page, size);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_LIST_FOUND, result);
	}

	@GetMapping("/groups/{groupId}")
	public ApiTemplate<AdminChallengeGroupDetailDto> getGroupDetail(@PathVariable Long groupId) {
		AdminChallengeGroupDetailDto result = challengeGroupQuery.getGroupDetailForAdmin(groupId);
		result.setLeaderMemberKey(clientHelper.requestMemberKey(result.getLeaderId()));
		result.setParticipantMemberKeys(clientHelper.requestMemberKeys(result.getParticipantIds()));

		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_DETAIL_FOUND, result);
	}

	@GetMapping("/{challengeId}/groups/participants")
	public ApiTemplate<PageTemplate<AdminTeamParticipantDto>> getChallengeParticipant(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminTeamParticipantDto> result =
			challengeGroupQuery.findParticipantByChallenge(challengeId, page, size);
		converter.convertPage(result);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_PARTICIPANTS_FOUND, result);
	}

	@GetMapping("/{challengeId}/groups/participants/excel")
	public void downloadChallengeParticipant(@PathVariable Long challengeId, HttpServletResponse response) {
		List<AdminTeamParticipantDto> result = challengeGroupQuery.findParticipantByChallengeForExcel(challengeId);
		converter.convert(result);
		excelDownloader.downloadAsStream(result, response);
	}
}
