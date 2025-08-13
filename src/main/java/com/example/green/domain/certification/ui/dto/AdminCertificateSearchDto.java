package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminCertificateSearchDto {
	private Long id;
	private Long challengeId;
	private ChallengeSnapshot challenge;
	private MemberSnapshot member;
	private LocalDate certifiedDate;
	private String imageUrl;
	private String review;
	private CertificationStatus status;

	public AdminCertificateSearchDto(
		Long id, ChallengeSnapshot challenge, MemberSnapshot member, LocalDate certifiedDate,
		String imageUrl, String review, CertificationStatus status
	) {
		this.id = id;
		this.challengeId = challenge.getChallengeId();
		this.challenge = challenge;
		this.member = member;
		this.certifiedDate = certifiedDate;
		this.imageUrl = imageUrl;
		this.review = review;
		this.status = status;
	}
}
