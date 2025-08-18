package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.domain.MemberSnapshot;

import lombok.AllArgsConstructor;
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

	@Data
	@AllArgsConstructor
	public static class ChallengeInfo {
		private Long id;
		private String name;
		private String code;
		private String image;
		private Integer point;
		private String groupCode;
		private String type;

		public static ChallengeInfo from(ChallengeSnapshot snapshot) {
			return new ChallengeInfo(
				snapshot.getChallengeId(),
				snapshot.getChallengeName(),
				snapshot.getChallengeCode(),
				snapshot.getChallengeImage(),
				snapshot.getChallengePoint(),
				snapshot.getGroupCode(),
				snapshot.getType()
			);
		}
	}

	@Data
	@AllArgsConstructor
	public static class MemberInfo {
		private Long id;
		private String key;

		public static MemberInfo from(MemberSnapshot snapshot) {
			return new MemberInfo(
				snapshot.getMemberId(),
				snapshot.getMemberKey()
			);
		}
	}

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
