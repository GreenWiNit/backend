package com.example.integration.excel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

class TestDto {

	private static AtomicLong ID_GENERATOR = new AtomicLong(1L);
	private static Random RANDOM = new Random();

	private Long id;
	private String name;
	private Integer age;
	private LocalDate birthDate;
	private Integer point;
	private LocalDateTime joinedAt;

	public TestDto() {
		long atomicId = ID_GENERATOR.getAndIncrement();
		int randomAge = RANDOM.nextInt(18, 65);
		int currentYear = LocalDate.now().getYear();
		int randomBirthYear = currentYear - randomAge;

		this.id = atomicId;
		this.name = "Test" + atomicId;
		this.age = randomAge;
		this.birthDate = LocalDate.of(
			randomBirthYear,
			RANDOM.nextInt(1, 13),
			RANDOM.nextInt(1, 29)
		);
		this.point = RANDOM.nextInt(1, 10001) * 100;
		this.joinedAt = LocalDateTime.of(
			currentYear - 1,
			RANDOM.nextInt(1, 13),
			RANDOM.nextInt(1, 29),
			RANDOM.nextInt(0, 24),
			RANDOM.nextInt(0, 60),
			RANDOM.nextInt(0, 60),
			RANDOM.nextInt(0, 1000) * 1_000_000
		);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public Integer getPoint() {
		return point;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}
}