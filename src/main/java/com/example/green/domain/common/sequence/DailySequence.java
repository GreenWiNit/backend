package com.example.green.domain.common.sequence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_sequences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySequence {

	@Id
	@Column(length = 50)
	private String sequenceKey;

	@Column(nullable = false)
	private Long currentValue;

	@Version
	private Long version;

	public DailySequence(String sequenceKey) {
		this.sequenceKey = sequenceKey;
		this.currentValue = 0L;
	}

	public Long getNextValue() {
		return ++currentValue;
	}
}