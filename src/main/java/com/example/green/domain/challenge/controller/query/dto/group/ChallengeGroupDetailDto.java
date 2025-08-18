package com.example.green.domain.challenge.controller.query.dto.group;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팀 챌린지 그룹 상세 응답")
public record ChallengeGroupDetailDto(
	@Schema(description = "그룹 ID", example = "1")
	Long id,
	@Schema(description = "그룹명", example = "강남구 러닝 그룹")
	String groupName,
	@Schema(description = "현재 참가자 수", example = "5")
	Integer currentParticipants,
	@Schema(description = "최대 참가자 수", example = "10")
	Integer maxParticipants,
	@Schema(description = "그룹 설명", example = "매주 화, 목 저녁 7시에 모여서 5km 러닝합니다.")
	String description,
	@Schema(description = "챌린지 시작 일자")
	LocalDate challengeDate,
	@Schema(description = "챌린지 시작 시간")
	LocalTime startTime,
	@Schema(description = "챌린지 종료 시간")
	LocalTime endTime,
	@Schema(description = "그룹 도로명 주소", example = "서울시 강남구 테헤란로 123")
	String roadAddress,
	@Schema(description = "그룹 상세 주소", example = "어느곳")
	String detailAddress,
	@Schema(description = "그룹 시군구", example = "강남구")
	String sigungu,
	@Schema(description = "그룹 전체 주소", example = "서울시 강남구 테헤란로 123 어느곳")
	String fullAddress,
	@Schema(description = "오픈 채팅 URL", example = "https://open.kakao.com/o/abc123")
	String openChatUrl,
	@Schema(description = "참여 여부")
	boolean participating
) {

	public static ChallengeGroupDetailDto from(ChallengeGroup challengeGroup, boolean participating) {
		return new ChallengeGroupDetailDto(
			challengeGroup.getId(),
			challengeGroup.getBasicInfo().getGroupName(),
			challengeGroup.getCapacity().getCurrentParticipants(),
			challengeGroup.getCapacity().getMaxParticipants(),
			challengeGroup.getBasicInfo().getDescription(),
			challengeGroup.getPeriod().getDate(),
			challengeGroup.getPeriod().getStartTime(),
			challengeGroup.getPeriod().getEndTime(),
			challengeGroup.getGroupAddress().getRoadAddress(),
			challengeGroup.getGroupAddress().getDetailAddress(),
			challengeGroup.getGroupAddress().getSigungu(),
			challengeGroup.getGroupAddress().getFullAddress(),
			challengeGroup.getBasicInfo().getOpenChatUrl(),
			participating
		);
	}
}
