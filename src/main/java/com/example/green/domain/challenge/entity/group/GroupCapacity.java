package com.example.green.domain.challenge.entity.group;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCapacity {

	@Column(nullable = false)
	private Integer currentParticipants;
	@Column(nullable = false)
	private Integer maxParticipants;

	private GroupCapacity(Integer maxParticipants) {
		if (maxParticipants != null && maxParticipants <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
		}
		this.currentParticipants = 0;
		this.maxParticipants = maxParticipants;
	}

	public static GroupCapacity of(Integer maxParticipants) {
		return new GroupCapacity(maxParticipants);
	}

	public boolean isFull() {
		return currentParticipants >= maxParticipants;
	}

	public void increase() {
		currentParticipants++;
	}

	public void decrease() {
		if (currentParticipants > 0) {
			currentParticipants--;
		}
	}
}