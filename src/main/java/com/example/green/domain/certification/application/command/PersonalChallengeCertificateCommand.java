package com.example.green.domain.certification.application.command;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;

public record PersonalChallengeCertificateCommand(
	Long memberId,
	Long challengeId,
	LocalDate challengeDate,
	String imageUrl,
	String review
) {

	public static PersonalChallengeCertificateCommand of(
		Long memberId, Long challengeId, LocalDate challengeDate, String imageUrl, String review) {
		return new PersonalChallengeCertificateCommand(memberId, challengeId, challengeDate, imageUrl, review);
	}

	public ChallengeCertification toEntity(MemberSnapshot member, ChallengeSnapshot challenge) {
		return ChallengeCertification.create(member, challenge, imageUrl, review, challengeDate);
	}
}
