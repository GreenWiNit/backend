package com.example.green.domain.common.idempotency;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.green.global.api.ApiTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

	private final IdemPotencyRepository idempotencyRepository;
	private final HttpServletRequest request;

	@Around("@annotation(Idempotent)")
	public Object checkIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
		String idempotencyKey = Optional.ofNullable(request.getHeader("idempotency-Key"))
			.orElseThrow(() -> new BusinessException(GlobalExceptionMessage.REQUIRED_IDEMPOTENCY_KEY));

		Optional<IdemPotency> optionalIdemPotency = idempotencyRepository.findById(idempotencyKey);
		if (optionalIdemPotency.isPresent()) {
			return optionalIdemPotency.get().toResponse();
		}

		Object result = joinPoint.proceed();
		IdemPotency newIdempotency = IdemPotency.of(idempotencyKey, (ApiTemplate<?>)result);
		idempotencyRepository.save(newIdempotency);

		return result;
	}
}