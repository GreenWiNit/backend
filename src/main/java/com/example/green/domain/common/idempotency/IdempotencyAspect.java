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

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

	private static final String LOCK_KEY_FORMAT = "idempotency:%s";

	private final IdemPotencyRepository idempotencyRepository;
	private final HttpServletRequest request;
	private final DistributedLockManager lockManager;

	@Around("@annotation(Idempotent)")
	public Object checkIdempotency(ProceedingJoinPoint joinPoint) {
		String idempotencyKey = extractIdempotencyKey();
		String lockKey = String.format(LOCK_KEY_FORMAT, idempotencyKey);

		return lockManager.executeWithLock(lockKey, () -> {
			Optional<IdemPotency> optionalIdemPotency = idempotencyRepository.findById(idempotencyKey);
			if (optionalIdemPotency.isPresent()) {
				return optionalIdemPotency.get().toResponse();
			}

			return process(joinPoint, idempotencyKey);
		});
	}

	private Object process(ProceedingJoinPoint joinPoint, String idempotencyKey) {
		try {
			Object result = joinPoint.proceed();
			IdemPotency newIdempotency = IdemPotency.of(idempotencyKey, (ApiTemplate<?>)result);
			idempotencyRepository.save(newIdempotency);
			return result;
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