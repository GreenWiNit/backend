package com.example.green;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class GreenApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreenApplication.class, args);
		System.out.println("Hello World!");
	}

}
