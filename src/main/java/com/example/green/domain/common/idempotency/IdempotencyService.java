package com.example.green.domain.common.idempotency;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

	private final IdemPotencyRepository idempotencyRepository;

	public Optional<IdemPotency> findExisting(String idempotencyKey) {
		return idempotencyRepository.findById(idempotencyKey);
	}

	@Transactional
	public IdemPotency saveResult(String idempotencyKey, Object result) {
		IdemPotency idemPotency = IdemPotency.of(idempotencyKey, result);
		return idempotencyRepository.save(idemPotency);
	}
}