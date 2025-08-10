package com.example.green.domain.challenge.controller.dto;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.entity.group.GroupAddress;
import com.example.green.domain.challenge.entity.group.GroupBasicInfo;
import com.example.green.domain.challenge.entity.group.GroupPeriod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "팀 챌린지 그룹 생성 요청")
public record ChallengeGroupCreateDto(
	@Schema(description = "그룹명", example = "강남구 러닝 그룹", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "그룹명은 필수값입니다.")
	@Size(max = 100, message = "그룹명은 100자 이하여야 합니다.")
	String groupName,

	@Schema(description = "도로명 주소", example = "서울시 강남구 테헤란로 123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "도로명 주소는 필수값입니다.")
	String roadAddress,

	@Schema(description = "상세 주소", example = "삼성동 빌딩 1층")
	String detailAddress,

	@Schema(description = "시군구", example = "강남구")
	String sigungu,

	@Schema(description = "그룹 설명", example = "매주 화, 목 저녁 7시에 모여서 5km 러닝합니다.")
	@Size(max = 500, message = "그룹 설명은 500자 이하여야 합니다.")
	String description,

	@Schema(description = "오픈 채팅 URL", example = "https://open.kakao.com/o/abc123")
	@Size(max = 500, message = "오픈 채팅 URL은 500자 이하여야 합니다.")
	String openChatUrl,

	@Schema(description = "그룹 시작 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "그룹 시작 일시는 필수값입니다.")
	LocalDateTime beginDateTime,

	@Schema(description = "그룹 종료 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "그룹 종료 일시는 필수값입니다.")
	LocalDateTime endDateTime,

	@Schema(description = "최대 참가자 수", example = "10")
	@Positive(message = "최대 참가자 수는 1명 이상이어야 합니다.")
	Integer maxParticipants
) {

	public ChallengeGroup toEntity(String teamCode, Long challengeId, Long leaderId) {
		GroupBasicInfo basicInfo = GroupBasicInfo.of(groupName, description, openChatUrl);
		GroupAddress address = GroupAddress.of(roadAddress, detailAddress, sigungu);
		GroupPeriod period = GroupPeriod.of(beginDateTime, endDateTime);
		return ChallengeGroup.create(teamCode, challengeId, leaderId, basicInfo, address, maxParticipants, period);
	}
}
