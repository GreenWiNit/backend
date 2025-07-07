package com.example.green.domain.auth.utils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

class RandomTokenGeneratorTest {

	@Test
	void 기본적으로_랜덤_생성자로_토큰이_생성된다() {
		// given
		RandomTokenGenerator randomTokenGenerator = new RandomTokenGenerator();

		// when
		String generate = randomTokenGenerator.generate(6);

		// then
		assertThat(generate).hasSize(6);
	}

	@Test
	void 매_생성마다_다른_토큰이_생성된다() {
		// given
		RandomTokenGenerator randomTokenGenerator = new RandomTokenGenerator();

		// when
		String generate1 = randomTokenGenerator.generate(6);
		String generate2 = randomTokenGenerator.generate(6);

		// then
		assertThat(generate1).isNotEqualTo(generate2);
	}

	@Test
	void 랜덤_객체로_토큰_값이_생성된다() {
		// given
		Random mockRandom = mock(Random.class);
		when(mockRandom.nextInt(anyInt())).thenReturn(0)
			.thenReturn(1)
			.thenReturn(2)
			.thenReturn(3)
			.thenReturn(4)
			.thenReturn(5);
		RandomTokenGenerator generator = new RandomTokenGenerator(mockRandom);

		// when
		String token = generator.generate(6);

		// then
		assertThat(token).isEqualTo("123456");
	}
}