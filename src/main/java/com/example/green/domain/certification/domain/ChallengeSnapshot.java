package com.example.green.domain.certification.domain;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
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

	@Column(nullable = false)
	private Long challengeId;
	@Column(nullable = false)
	private String challengeTitle;
	@Column(nullable = false)
	private String challengeCode;
	private String teamCode;
	private ChallengeType type;

	private ChallengeSnapshot(Long id, String title, String code, String teamCode, ChallengeType type) {
		EntityValidator.validateAutoIncrementId(id, "challengeId 필수 값 입니다.");
		EntityValidator.validateEmptyString(title, "challengeTitle 필수 값 입니다.");
		EntityValidator.validateEmptyString(code, "challengeCode 필수 값 입니다.");
		EntityValidator.validateEmptyString(teamCode, "teamCode 필수 값입니다.");
		this.challengeId = id;
		this.challengeTitle = title;
		this.challengeCode = code;
		this.teamCode = teamCode;
		this.type = type;
	}

	public static ChallengeSnapshot ofPersonal(Long id, String title, String code) {
		return new ChallengeSnapshot(id, title, code, null, ChallengeType.PERSONAL);
	}

	public static ChallengeSnapshot ofTeam(Long id, String title, String code, String teamCode) {
		return new ChallengeSnapshot(id, title, code, teamCode, ChallengeType.TEAM);
	}
}
