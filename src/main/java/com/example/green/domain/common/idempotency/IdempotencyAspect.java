package com.example.green.domain.common.idempotency;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.green.domain.common.lock.DistributedLockManager;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.utils.ThreadUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {

	private static final String LOCK_KEY_FORMAT = "idempotency:%s";
	private static final int MAX_RETRY_ATTEMPTS = 5;
	private static final long BASE_RETRY_DELAY_MS = 50;

	private final IdemPotencyRepository idempotencyRepository;
	private final HttpServletRequest request;
	private final ThreadUtils threadUtils;
	private final DistributedLockManager lockManager;

	@Around("@annotation(Idempotent)")
	public Object handleIdempotentRequest(ProceedingJoinPoint joinPoint) {
		String idempotencyKey = extractIdempotencyKey();
		String lockKey = String.format(LOCK_KEY_FORMAT, idempotencyKey);

		for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
			try {
				return executeIdempotentLogic(joinPoint, lockKey, idempotencyKey);
			} catch (IllegalStateException e) {
				threadUtils.waitWithBackoff(BASE_RETRY_DELAY_MS, attempt);
			}
		}
		throw new IllegalStateException("멱등키 처리 실패" + lockKey);
	}

	private Object executeIdempotentLogic(ProceedingJoinPoint joinPoint, String lockKey, String idempotencyKey) {
		return lockManager.executeWithLock(lockKey, () -> {
			Optional<IdemPotency> existing = idempotencyRepository.findById(idempotencyKey);
			if (existing.isPresent()) {
				return existing.get().toResponse();
			}

			Object result = executeBusinessLogic(joinPoint);
			IdemPotency newIdempotency = IdemPotency.of(idempotencyKey, (ApiTemplate<?>)result);
			idempotencyRepository.save(newIdempotency);
			return result;
		});
	}

	private Object executeBusinessLogic(ProceedingJoinPoint joinPoint) {
		try {
			return joinPoint.proceed();
		} catch (BusinessException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private String extractIdempotencyKey() {
		return Optional.ofNullable(request.getHeader("idempotency-Key"))
			.orElseThrow(() -> new BusinessException(GlobalExceptionMessage.REQUIRED_IDEMPOTENCY_KEY));
	}
}