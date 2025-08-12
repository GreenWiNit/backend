package com.example.green.domain.certification.application.command;

public record TeamChallengeCertificateCommand(
	Long memberId,
	Long groupId,
	String imageUrl,
	String review
) {

	public static TeamChallengeCertificateCommand of(Long memberId, Long groupId, String imageUrl, String review) {
		return new TeamChallengeCertificateCommand(memberId, groupId, imageUrl, review);
	}
}
