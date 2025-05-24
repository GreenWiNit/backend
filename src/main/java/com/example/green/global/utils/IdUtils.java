package com.example.green.global.utils;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class IdUtils {

	private final Supplier<String> uuidSupplier;

	public IdUtils() {
		this(() -> UUID.randomUUID().toString());
	}

	public IdUtils(Supplier<String> uuidSupplier) {
		this.uuidSupplier = uuidSupplier;
	}

	public String generateUniqueId(int length) {
		return uuidSupplier.get().substring(0, length);
	}
}
