package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminPersonalChallengeQueryControllerDocs;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/personal")
@RequiredArgsConstructor
public class AdminPersonalChallengeQueryController implements AdminPersonalChallengeQueryControllerDocs {

	private final PersonalChallengeQuery personalChallengeQuery;

	@GetMapping
	public ApiTemplate<CursorTemplate<Long, AdminPersonalChallengesDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		CursorTemplate<Long, AdminPersonalChallengesDto> result =
			personalChallengeQuery.findAllForAdminByCursor(cursor, size);
		return ApiTemplate.ok(PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getPersonalChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = personalChallengeQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}
}
