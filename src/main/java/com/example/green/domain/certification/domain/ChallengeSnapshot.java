package com.example.green.domain.certification.domain;

import com.example.green.global.utils.EntityValidator;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeSnapshot {

	public static final String TEAM_TYPE = "T";
	public static final String PERSONAL_TYPE = "P";

	@Column(nullable = false)
	private Long challengeId;
	@Column(nullable = false)
	private String challengeName;
	@Column(nullable = false)
	private String challengeCode;
	@Column(length = 512, nullable = false)
	private String challengeImage;
	private Integer challengePoint;
	private String groupCode;
	private String type;

	private ChallengeSnapshot(
		Long id, String name, String code, String groupCode, Integer point, String type, String imageUrl
	) {
		EntityValidator.validateAutoIncrementId(id, "challengeId 필수 값 입니다.");
		EntityValidator.validateEmptyString(name, "challengeName 필수 값 입니다.");
		EntityValidator.validateEmptyString(code, "challengeCode 필수 값 입니다.");
		EntityValidator.validateEmptyString(imageUrl, "challengeImage 필수 값 입니다.");
		this.challengeId = id;
		this.challengeName = name;
		this.challengeCode = code;
		this.challengeImage = imageUrl;
		this.groupCode = groupCode;
		this.challengePoint = point;
		this.type = type;
	}

	public static ChallengeSnapshot ofPersonal(Long id, String name, String code, Integer point, String imageUrl) {
		return new ChallengeSnapshot(id, name, code, null, point, PERSONAL_TYPE, imageUrl);
	}

	public static ChallengeSnapshot ofTeam(
		Long id, String name, String code, Integer point, String groupCode, String imageUrl) {
		EntityValidator.validateEmptyString(groupCode, "GroupCode 필수 값입니다.");
		return new ChallengeSnapshot(id, name, code, groupCode, point, TEAM_TYPE, imageUrl);
	}

	public static boolean isValidType(String type) {
		return type.equals(TEAM_TYPE) || type.equals(PERSONAL_TYPE);
	}
}
