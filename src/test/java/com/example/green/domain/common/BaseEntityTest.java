package com.example.green.domain.common;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BaseEntityTest {

	@Test
	void BaseEntity는_soft_delete를_지원한다() {
		// given
		BaseEntity baseEntity = new BaseEntity();

		// when
		baseEntity.markDeleted();

		// then
		assertThat(baseEntity.isDeleted()).isTrue();
	}

	@Test
	void BaseEntity는_제거된_파일_복원을_지원한다() {
		// given
		BaseEntity baseEntity = new BaseEntity();
		baseEntity.markDeleted();

		// when
		baseEntity.restore();

		// then
		assertThat(baseEntity.isDeleted()).isFalse();
	}

}