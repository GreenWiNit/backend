package com.example.green.domain.auth.entity.verification;

@FunctionalInterface
public interface TokenGenerator {

	String generate(int size);
}
