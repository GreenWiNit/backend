package com.example.green.domain.challenge.entity.challenge;

import java.time.LocalDateTime;

import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseChallengeParticipation extends BaseEntity {

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Integer certCount;

	@Column(nullable = false)
	private LocalDateTime participatedAt;

	protected BaseChallengeParticipation(
		Long memberId,
		LocalDateTime participatedAt
	) {
		this.certCount = 0;
		this.memberId = memberId;
		this.participatedAt = participatedAt;
	}

	public boolean isParticipated(Long memberId) {
		return this.memberId.equals(memberId);
	}
}
