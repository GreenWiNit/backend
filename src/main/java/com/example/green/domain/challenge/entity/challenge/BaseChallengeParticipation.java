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

	private Long memberId;

	@Column(nullable = false)
	private LocalDateTime participatedAt;

	// 하위 클래스를 위한 protected 생성자
	protected BaseChallengeParticipation(
		Long memberId,
		LocalDateTime participatedAt
	) {
		this.memberId = memberId;
		this.participatedAt = participatedAt;
	}

	public boolean isParticipated(Long memberId) {
		return this.memberId.equals(memberId);
	}
}
