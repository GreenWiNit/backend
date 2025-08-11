package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage.*;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminPersonalChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.PersonalParticipationDto;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.util.ClientHelper;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.excel.core.ExcelDownloader;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/personal")
@RequiredArgsConstructor
public class AdminPersonalChallengeQueryController implements AdminPersonalChallengeQueryControllerDocs {

	private final PersonalChallengeQuery personalChallengeQuery;
	private final ExcelDownloader excelDownloader;
	private final ClientHelper clientHelper;

	@GetMapping
	public ApiTemplate<PageTemplate<AdminPersonalChallengesDto>> getPersonalChallenges(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminPersonalChallengesDto> result = personalChallengeQuery.findChallengePage(page, size);
		return ApiTemplate.ok(PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getPersonalChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = personalChallengeQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/excel")
	public void downloadChallengeExcel(HttpServletResponse response) {
		List<AdminPersonalChallengesDto> result = personalChallengeQuery.findChallengePageForExcel();
		excelDownloader.downloadAsStream(result, response);
	}

	@GetMapping("/{challengeId}/participation")
	public ApiTemplate<PageTemplate<PersonalParticipationDto>> getChallengeParticipant(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<PersonalParticipationDto> result =
			personalChallengeQuery.findParticipationByChallenge(challengeId, page, size);

		List<Long> participantIds = result.content().stream().map(PersonalParticipationDto::getMemberId).toList();
		Map<Long, String> memberKeyById = clientHelper.requestMemberKeyById(participantIds);
		result.content().forEach(dto -> dto.setMemberKey(memberKeyById.get(dto.getMemberId())));

		return ApiTemplate.ok(CHALLENGE_PARTICIPANTS_FOUND, result);
	}
}
