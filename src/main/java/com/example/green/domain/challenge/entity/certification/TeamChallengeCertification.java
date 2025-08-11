package com.example.green.domain.challenge.entity.certification;

import static com.example.green.global.utils.EntityValidator.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.TeamChallengeParticipation;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	indexes = {
		@Index(name = "idx_team_cert_participation", columnList = "participation_id"),
		@Index(name = "idx_team_cert_date", columnList = "certified_at"),
		@Index(name = "idx_team_cert_member", columnList = "member_id")
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_team_cert_once_per_day",
			columnNames = {"participation_id", "certified_date"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class TeamChallengeCertification extends BaseChallengeCertification {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "participation_id", nullable = false)
	private TeamChallengeParticipation participation;

	public static TeamChallengeCertification create(
		TeamChallengeParticipation participation,
		Member participant,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		// 필수 값 validate
		validateNullData(participation, "챌린지 참여 정보는 필수값입니다.");
		validateEmptyString(certificationImageUrl, "인증 이미지는 필수값입니다.");
		validateNullData(certifiedAt, "인증 시각은 필수값입니다.");
		validateNullData(certifiedDate, "인증 날짜는 필수값입니다.");

		return new TeamChallengeCertification(
			participation,
			participant,
			certificationImageUrl,
			certificationReview,
			certifiedAt,
			certifiedDate
		);
	}

	private TeamChallengeCertification(
		TeamChallengeParticipation participation,
		Member participant,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		super(participant, certificationImageUrl, certificationReview, certifiedAt, certifiedDate);
		this.participation = participation;
		validateCertificationData();
	}
}
