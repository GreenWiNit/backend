package com.example.green.domain.auth.entity.verification.generator;

import java.util.Random;

import com.example.green.domain.auth.entity.verification.TokenGenerator;

public class RandomTokenGenerator implements TokenGenerator {

	private static final String TOKEN_DATASOURCE = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private final Random random;

	public RandomTokenGenerator() {
		this(new Random());
	}

	public RandomTokenGenerator(Random random) {
		this.random = random;
	}

	@Override
	public String generate(int size) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			builder.append(TOKEN_DATASOURCE.charAt(random.nextInt(TOKEN_DATASOURCE.length())));
		}
		return builder.toString();
	}
}
