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

	private static final String TEAM_TYPE = "T";
	private static final String PERSONAL_TYPE = "P";

	@Column(nullable = false)
	private Long challengeId;
	@Column(nullable = false)
	private String challengeName;
	@Column(nullable = false)
	private String challengeCode;
	private String groupCode;
	private String type;

	private ChallengeSnapshot(Long id, String name, String code, String groupCode, String type) {
		EntityValidator.validateAutoIncrementId(id, "challengeId 필수 값 입니다.");
		EntityValidator.validateEmptyString(name, "challengeName 필수 값 입니다.");
		EntityValidator.validateEmptyString(code, "challengeCode 필수 값 입니다.");
		this.challengeId = id;
		this.challengeName = name;
		this.challengeCode = code;
		this.groupCode = groupCode;
		this.type = type;
	}

	public static ChallengeSnapshot ofPersonal(Long id, String name, String code) {
		return new ChallengeSnapshot(id, name, code, null, PERSONAL_TYPE);
	}

	public static ChallengeSnapshot ofTeam(Long id, String name, String code, String groupCode) {
		EntityValidator.validateEmptyString(groupCode, "GroupCode 필수 값입니다.");
		return new ChallengeSnapshot(id, name, code, groupCode, TEAM_TYPE);
	}
}
