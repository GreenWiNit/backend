package com.example.green.domain.challenge.entity.group;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class GroupCapacity {

	@Column(nullable = false)
	private Integer currentParticipants;
	@Column(nullable = false)
	private Integer maxParticipants;

	private GroupCapacity(Integer currentParticipants, Integer maxParticipants) {
		if (maxParticipants == null || maxParticipants <= 0) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_MAX_PARTICIPANTS_COUNT);
		}
		if (currentParticipants > maxParticipants) {
			throw new ChallengeException(ChallengeExceptionMessage.MAX_PARTICIPANTS_LESS_THAN_CURRENT);
		}
		this.maxParticipants = maxParticipants;
		this.currentParticipants = currentParticipants;
	}

	public static GroupCapacity of(Integer maxParticipants) {
		return new GroupCapacity(0, maxParticipants);
	}

	public static GroupCapacity update(Integer currentParticipants, Integer maxParticipants) {
		return new GroupCapacity(currentParticipants, maxParticipants);
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