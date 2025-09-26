package com.example.green.domain.common.idempotency;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

	private final IdempotencyService idempotencyService;
	private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();
	private final HttpServletRequest request;

	@Around("@annotation(Idempotent)")
	public Object handleIdempotentRequest(ProceedingJoinPoint joinPoint) throws Throwable {
		String idempotencyKey = extractIdempotencyKey();
		Object lock = lockMap.computeIfAbsent(idempotencyKey, k -> new Object());

		synchronized (lock) {
			try {
				return executeIdempotent(joinPoint, idempotencyKey);
			} finally {
				lockMap.remove(idempotencyKey);
			}
		}
	}

	private ApiTemplate<?> executeIdempotent(ProceedingJoinPoint joinPoint, String idempotencyKey) throws Throwable {
		Optional<IdemPotency> existing = idempotencyService.findExisting(idempotencyKey);
		if (existing.isPresent()) {
			return existing.get().toResponse();
		}
		Object result = joinPoint.proceed();
		IdemPotency idemPotency = idempotencyService.saveResult(idempotencyKey, result);
		return idemPotency.toResponse();
	}

	private String extractIdempotencyKey() {
		return Optional.ofNullable(request.getHeader("idempotency-Key"))
			.orElseThrow(() -> new BusinessException(GlobalExceptionMessage.REQUIRED_IDEMPOTENCY_KEY));
	}
}