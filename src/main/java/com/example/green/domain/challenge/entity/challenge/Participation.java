package com.example.green.domain.challenge.entity.challenge;

import com.example.green.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_participations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Integer certCount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "challenge_id", nullable = false)
	private Challenge challenge;

	private Participation(Challenge challenge, Long memberId) {
		this.challenge = challenge;
		this.memberId = memberId;
		this.certCount = 0;
	}

	static Participation create(Challenge challenge, Long memberId) {
		return new Participation(challenge, memberId);
	}

	public boolean isParticipated(Long memberId) {
		return this.memberId.equals(memberId);
	}
}
