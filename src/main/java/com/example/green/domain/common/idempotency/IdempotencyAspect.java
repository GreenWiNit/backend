package com.example.green.domain.common.idempotency;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
	private final HttpServletRequest request;

	@Around("@annotation(Idempotent)")
	public Object handleIdempotentRequest(ProceedingJoinPoint joinPoint) {
		String idempotencyKey = extractIdempotencyKey();
		return idempotencyService.executeIdempotent(idempotencyKey, () -> executeBusinessLogic(joinPoint));
	}

	private Object executeBusinessLogic(ProceedingJoinPoint joinPoint) {
		try {
			return joinPoint.proceed();
		} catch (BusinessException e) {
			log.error("멱등성 비즈니스 예외: ", e);
			throw e;
		} catch (Throwable e) {
			log.error("멱등성 알 수 없는 예외: ", e);
			throw new RuntimeException(e);
		}
	}

	private String extractIdempotencyKey() {
		return Optional.ofNullable(request.getHeader("idempotency-Key"))
			.orElseThrow(() -> new BusinessException(GlobalExceptionMessage.REQUIRED_IDEMPOTENCY_KEY));
	}
}