package com.example.green.unit.dummy;

import org.apache.logging.log4j.util.InternalException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

import jakarta.validation.Valid;

@RestController
public class ErrorDummyController {

	@GetMapping("/business-exception")
	public void occurBusinessException() {
		throw new BusinessException(new ExceptionMessage() {
			@Override
			public HttpStatus getHttpStatus() {
				return HttpStatus.BAD_GATEWAY;
			}

			@Override
			public String getMessage() {
				return "에러 발생";
			}
		});
	}

	@GetMapping("/internal-server-exception")
	public void occurInternalServerException() {
		throw new InternalException("서버 에러 발생!");
	}

	@GetMapping("/no-endpoint")
	public void occurNoResourceFoundException() throws NoResourceFoundException {
		throw new NoResourceFoundException(HttpMethod.GET, "/no-endpoint");
	}

	@PostMapping("/invalid-request-body")
	public void occurRequestBodyValidationException(@Valid @RequestBody ValidationDto dto) {
	}

	@GetMapping("/invalid-model-attribute")
	public void occurModelAttributeValidationException(@Valid @ModelAttribute ValidationDto dto) {
	}

	@GetMapping("/query-parameter-request")
	public void occurQueryParameterException(@Valid @RequestParam int number) {
	}

	@GetMapping("/path-variable-request/{path-variable}")
	public void occurPathVariableException(@Valid @PathVariable(name = "path-variable") int pathVariable) {
	}

}
