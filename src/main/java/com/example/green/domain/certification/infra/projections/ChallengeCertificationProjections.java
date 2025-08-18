package com.example.green.domain.certification.infra.projections;

import static com.example.green.domain.certification.domain.QChallengeCertification.*;

import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeCertificationProjections {

	public static ConstructorExpression<ChallengeCertificationDto> toCertificationByPersonal() {
		return Projections.constructor(ChallengeCertificationDto.class,
			challengeCertification.id,
			challengeCertification.challenge.challengeName,
			challengeCertification.challenge.challengeImage,
			challengeCertification.certifiedDate,
			challengeCertification.status
		);
	}

	public static ConstructorExpression<AdminCertificateSearchDto> toSearch() {
		return Projections.constructor(AdminCertificateSearchDto.class,
			challengeCertification.id,
			challengeCertification.challenge,
			challengeCertification.member,
			challengeCertification.certifiedDate,
			challengeCertification.imageUrl,
			challengeCertification.review,
			challengeCertification.status
		);
	}
}
