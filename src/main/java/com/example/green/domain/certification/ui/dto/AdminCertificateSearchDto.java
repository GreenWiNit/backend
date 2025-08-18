package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;
import com.example.green.domain.certification.ui.dto.sub.ChallengeInfo;
import com.example.green.domain.certification.ui.dto.sub.MemberInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminCertificateSearchDto {
	private Long id;
	private ChallengeInfo challenge;
	private MemberInfo member;
	private LocalDate certifiedDate;
	private String imageUrl;
	private String review;
	private CertificationStatus status;

	public AdminCertificateSearchDto(
		Long id, ChallengeSnapshot challenge, MemberSnapshot member, LocalDate certifiedDate,
		String imageUrl, String review, CertificationStatus status
	) {
		this.id = id;
		this.challenge = ChallengeInfo.from(challenge);
		this.member = MemberInfo.from(member);
		this.certifiedDate = certifiedDate;
		this.imageUrl = imageUrl;
		this.review = review;
		this.status = status;
	}
}
