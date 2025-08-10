package com.example.green.domain.challengecert.entity;

import java.time.LocalDateTime;

import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseChallengeParticipation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
}
