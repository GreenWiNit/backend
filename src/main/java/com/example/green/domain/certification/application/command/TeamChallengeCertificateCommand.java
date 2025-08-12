package com.example.green.domain.certification.application.command;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;

public record TeamChallengeCertificateCommand(
	Long memberId,
	Long groupId,
	String imageUrl,
	String review
) {

	public static TeamChallengeCertificateCommand of(Long memberId, Long groupId, String imageUrl, String review) {
		return new TeamChallengeCertificateCommand(memberId, groupId, imageUrl, review);
	}

	public ChallengeCertification toEntity(MemberSnapshot member, ChallengeSnapshot challenge, LocalDate certDate) {
		return ChallengeCertification.create(member, challenge, imageUrl, review, certDate);
	}
}
