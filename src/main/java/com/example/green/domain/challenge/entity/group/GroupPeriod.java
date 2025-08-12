package com.example.green.domain.challenge.entity.group;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@EqualsAndHashCode
@Getter
public class GroupPeriod {

	@Column(nullable = false)
	private LocalDateTime beginDateTime;

	@Column(nullable = false)
	private LocalDateTime endDateTime;

	private GroupPeriod(LocalDateTime beginDateTime, LocalDateTime endDateTime) {
		if (beginDateTime.isAfter(endDateTime)) {
			throw new ChallengeException(ChallengeExceptionMessage.INVALID_GROUP_PERIOD);
		}
		this.beginDateTime = beginDateTime;
		this.endDateTime = endDateTime;
	}

	public static GroupPeriod of(LocalDateTime beginDateTime, LocalDateTime endDateTime) {
		return new GroupPeriod(beginDateTime, endDateTime);
	}

	public boolean canParticipate(LocalDateTime now) {
		return now.isBefore(beginDateTime);
	}

	public LocalDate getDate() {
		return beginDateTime.toLocalDate();
	}

	public LocalTime getStartTime() {
		return beginDateTime.toLocalTime();
	}

	public LocalTime getEndTime() {
		return endDateTime.toLocalTime();
	}
}