package com.example.green.domain.common.idempotency;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.global.api.ApiTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

	private final IdemPotencyRepository idempotencyRepository;
	private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();

	public Object executeIdempotent(String idempotencyKey, Supplier<Object> businessLogic) {
		Object lock = lockMap.computeIfAbsent(idempotencyKey, k -> new Object());

		synchronized (lock) {
			try {
				Optional<IdemPotency> existing = findExisting(idempotencyKey);
				return executeBasedOnExistence(idempotencyKey, existing, businessLogic);
			} finally {
				lockMap.remove(idempotencyKey);
			}
		}
	}

	private Object executeBasedOnExistence(String idempotencyKey, Optional<IdemPotency> existing,
		Supplier<Object> businessLogic) {
		if (existing.isPresent()) {
			log.debug("기존 멱등성 응답 반환: {}", idempotencyKey);
			return existing.get().toResponse();
		}

		log.debug("멱등성 비즈니스 로직 실행: {}", idempotencyKey);
		Object result = businessLogic.get();
		saveResult(idempotencyKey, result);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public Optional<IdemPotency> findExisting(String idempotencyKey) {
		return idempotencyRepository.findById(idempotencyKey);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveResult(String idempotencyKey, Object result) {
		try {
			saveIfNotExists(idempotencyKey, (ApiTemplate<?>)result);
		} catch (Exception e) {
			log.debug("멱등성 데이터 저장 중복 (무시): {} - {}", idempotencyKey, e.getMessage());
		}
	}

	private void saveIfNotExists(String idempotencyKey, ApiTemplate<?> result) {
		if (idempotencyRepository.findById(idempotencyKey).isEmpty()) {
			IdemPotency newIdempotency = IdemPotency.of(idempotencyKey, result);
			idempotencyRepository.save(newIdempotency);
			log.debug("멱등성 데이터 저장 완료: {}", idempotencyKey);
		}
	}
}