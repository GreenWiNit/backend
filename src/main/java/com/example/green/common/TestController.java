package com.example.green.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping(value = "/")
	public String doGetHelloWorld() {
		return "Hello World";
	}
}
