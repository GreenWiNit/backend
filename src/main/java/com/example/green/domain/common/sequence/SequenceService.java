package com.example.green.domain.common.sequence;

import java.time.LocalDateTime;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SequenceService {

	private final DailySequenceRepository sequenceRepository;

	@Retryable(value = OptimisticLockingFailureException.class, maxAttempts = 5)
	public long getNextSequence(SequenceType type, LocalDateTime date) {
		String sequenceKey = type.getSequenceKey(date);
		DailySequence sequence = sequenceRepository.findById(sequenceKey)
			.orElse(new DailySequence(sequenceKey));

		Long nextValue = sequence.getNextValue();
		sequenceRepository.save(sequence);

		return nextValue;
	}

	public String generateCode(SequenceType type, LocalDateTime date) {
		long sequence = getNextSequence(type, date);
		return type.generateCode(date, sequence);
	}
}