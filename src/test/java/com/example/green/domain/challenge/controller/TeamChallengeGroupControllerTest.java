package com.example.green.domain.challenge.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.enums.GroupStatus;
import com.example.green.domain.challenge.service.TeamChallengeGroupService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(TeamChallengeGroupController.class)
class TeamChallengeGroupControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private TeamChallengeGroupService teamChallengeGroupService;

	@Test
	void 팀_챌린지_그룹_목록을_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		LocalDateTime now = LocalDateTime.now();

		List<TeamChallengeGroupListResponseDto> groups = List.of(
			new TeamChallengeGroupListResponseDto(
				1L, "테스트 그룹 1", "서울시 강남구 테헤란로 123",
				now.minusHours(1), now.plusHours(1), 5, 10, GroupStatus.RECRUITING, false
			),
			new TeamChallengeGroupListResponseDto(
				2L, "테스트 그룹 2", "서울시 강남구 테헤란로 456",
				now.minusHours(1), now.plusHours(1), 3, 10, GroupStatus.RECRUITING, true
			)
		);
		CursorTemplate<Long, TeamChallengeGroupListResponseDto> mockResult = CursorTemplate.of(groups);

		when(teamChallengeGroupService.getTeamChallengeGroups(any(), any(), any()))
			.thenReturn(mockResult);

		// when
		ApiTemplate<CursorTemplate<Long, TeamChallengeGroupListResponseDto>> response = RestAssuredMockMvc
			.given().log().all()
			.when()
			.get("/api/challenges/{challengeId}/groups", challengeId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response.success()).isTrue();
		assertThat(response.result()).isNotNull();
		assertThat(response.result().content()).hasSize(2);
		assertThat(response.result().content().get(0).groupName()).isEqualTo("테스트 그룹 1");
		assertThat(response.result().content().get(1).isLeader()).isTrue();

		verify(teamChallengeGroupService).getTeamChallengeGroups(eq(challengeId), any(), any());
	}

	@Test
	void 커서_파라미터로_그룹_목록을_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		Long cursor = 10L;

		CursorTemplate<Long, TeamChallengeGroupListResponseDto> mockResult = CursorTemplate.ofEmpty();

		when(teamChallengeGroupService.getTeamChallengeGroups(any(), any(), any()))
			.thenReturn(mockResult);

		// when
		ApiTemplate<CursorTemplate<Long, TeamChallengeGroupListResponseDto>> response = RestAssuredMockMvc
			.given().log().all()
			.queryParam("cursor", cursor)
			.when()
			.get("/api/challenges/{challengeId}/groups", challengeId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response.success()).isTrue();
		assertThat(response.result().content()).isEmpty();

		verify(teamChallengeGroupService).getTeamChallengeGroups(any(), any(), any());
	}

	@Test
	void 팀_챌린지_그룹을_생성할_수_있다() {
		// given
		Long challengeId = 1L;
		Long expectedGroupId = 100L;
		LocalDateTime now = LocalDateTime.now();

		TeamChallengeGroupCreateRequestDto request = new TeamChallengeGroupCreateRequestDto(
			"새 그룹",
			"서울시 강남구 테헤란로 123",
			"삼성동 빌딩 1층",
			"새 그룹 설명",
			"https://openchat.example.com",
			now.plusHours(1),
			now.plusHours(3),
			15
		);

		when(teamChallengeGroupService.createTeamChallengeGroup(any(), any(), any()))
			.thenReturn(expectedGroupId);

		// when
		ApiTemplate<Long> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when()
			.post("/api/challenges/{challengeId}/groups", challengeId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response.success()).isTrue();
		assertThat(response.result()).isEqualTo(expectedGroupId);

		verify(teamChallengeGroupService).createTeamChallengeGroup(any(), any(), any());
	}

	@Test
	void 그룹_생성_시_필수값이_누락되면_400_에러가_발생한다() {
		// given
		Long challengeId = 1L;

		TeamChallengeGroupCreateRequestDto invalidRequest = new TeamChallengeGroupCreateRequestDto(
			"", // 빈 그룹명
			"서울시 강남구",
			null,
			null,
			null,
			LocalDateTime.now().plusHours(1),
			LocalDateTime.now().plusHours(3),
			10
		);

		// when & then
		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(invalidRequest)
			.when()
			.post("/api/challenges/{challengeId}/groups", challengeId)
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST);

		verify(teamChallengeGroupService, never()).createTeamChallengeGroup(any(), any(), any());
	}

	@Test
	void 팀_챌린지_그룹_상세_정보를_조회할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		LocalDateTime now = LocalDateTime.now();

		TeamChallengeGroupDetailResponseDto mockResult = new TeamChallengeGroupDetailResponseDto(
			groupId, "테스트 그룹", "서울시 강남구 테헤란로 123",
			"테스트 그룹 설명", "https://openchat.example.com",
			now.minusHours(1), now.plusHours(1), 5, 10,
			GroupStatus.RECRUITING, true, true
		);

		when(teamChallengeGroupService.getTeamChallengeGroupDetail(any(), any()))
			.thenReturn(mockResult);

		// when
		ApiTemplate<TeamChallengeGroupDetailResponseDto> response = RestAssuredMockMvc
			.given().log().all()
			.when()
			.get("/api/challenges/groups/{groupId}", groupId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(new TypeRef<>() {
			});

		// then
		assertThat(response.success()).isTrue();
		assertThat(response.result().id()).isEqualTo(groupId);
		assertThat(response.result().groupName()).isEqualTo("테스트 그룹");
		assertThat(response.result().isLeader()).isTrue();
		assertThat(response.result().isParticipant()).isTrue();

		verify(teamChallengeGroupService).getTeamChallengeGroupDetail(any(), any());
	}

	@Test
	void 팀_챌린지_그룹에_참가할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;

		doNothing().when(teamChallengeGroupService).joinTeamChallengeGroup(any(), any());

		// when
		NoContent response = RestAssuredMockMvc
			.given().log().all()
			.when()
			.post("/api/challenges/groups/{groupId}", groupId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(NoContent.class);

		// then
		assertThat(response.success()).isTrue();

		verify(teamChallengeGroupService).joinTeamChallengeGroup(any(), any());
	}

	@Test
	void 팀_챌린지_그룹_정보를_수정할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;
		LocalDateTime now = LocalDateTime.now();

		TeamChallengeGroupUpdateRequestDto request = new TeamChallengeGroupUpdateRequestDto(
			"수정된 그룹명",
			"서울시 강남구 테헤란로 456",
			"수정된 상세 주소",
			"수정된 설명",
			"https://new-openchat.example.com",
			now.plusHours(2),
			now.plusHours(4),
			20
		);

		doNothing().when(teamChallengeGroupService).updateTeamChallengeGroup(any(), any(), any());

		// when
		NoContent response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when()
			.put("/api/challenges/groups/{groupId}", groupId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(NoContent.class);

		// then
		assertThat(response.success()).isTrue();

		verify(teamChallengeGroupService).updateTeamChallengeGroup(any(), any(), any());
	}

	@Test
	void 팀_챌린지_그룹을_삭제할_수_있다() {
		// given
		Long challengeId = 1L;
		Long groupId = 1L;

		doNothing().when(teamChallengeGroupService).deleteTeamChallengeGroup(any(), any());

		// when
		NoContent response = RestAssuredMockMvc
			.given().log().all()
			.when()
			.delete("/api/challenges/groups/{groupId}", groupId)
			.then().log().all()
			.status(HttpStatus.OK)
			.extract()
			.as(NoContent.class);

		// then
		assertThat(response.success()).isTrue();

		verify(teamChallengeGroupService).deleteTeamChallengeGroup(any(), any());
	}
}
