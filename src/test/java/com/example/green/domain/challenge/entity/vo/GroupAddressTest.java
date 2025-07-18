package com.example.green.domain.challenge.entity.vo;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.global.error.exception.BusinessException;

class GroupAddressTest {

	@Test
	void 도로명_주소만으로_GroupAddress를_생성할_수_있다() {
		// given
		String roadAddress = "서울시 강남구 테헤란로 123";

		// when
		GroupAddress groupAddress = GroupAddress.of(roadAddress);

		// then
		assertThat(groupAddress.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(groupAddress.getDetailAddress()).isNull();
		assertThat(groupAddress.getFullAddress()).isEqualTo(roadAddress);
	}

	@Test
	void 도로명_주소와_상세_주소로_GroupAddress를_생성할_수_있다() {
		// given
		String roadAddress = "서울시 강남구 테헤란로 123";
		String detailAddress = "삼성동 빌딩 1층";

		// when
		GroupAddress groupAddress = GroupAddress.of(roadAddress, detailAddress);

		// then
		assertThat(groupAddress.getRoadAddress()).isEqualTo(roadAddress);
		assertThat(groupAddress.getDetailAddress()).isEqualTo(detailAddress);
		assertThat(groupAddress.getFullAddress()).isEqualTo(roadAddress + " " + detailAddress);
	}

	@Test
	void 상세_주소가_null이면_도로명_주소만_반환한다() {
		// given
		String roadAddress = "서울시 강남구 테헤란로 123";

		// when
		GroupAddress groupAddress = GroupAddress.of(roadAddress, null);

		// then
		assertThat(groupAddress.getFullAddress()).isEqualTo(roadAddress);
	}

	@Test
	void 상세_주소가_빈_문자열이면_도로명_주소만_반환한다() {
		// given
		String roadAddress = "서울시 강남구 테헤란로 123";
		String emptyDetailAddress = "   ";

		// when
		GroupAddress groupAddress = GroupAddress.of(roadAddress, emptyDetailAddress);

		// then
		assertThat(groupAddress.getFullAddress()).isEqualTo(roadAddress);
	}

	@Test
	void 도로명_주소가_null이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> GroupAddress.of(null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 도로명_주소가_빈_문자열이면_예외가_발생한다() {
		// when & then
		assertThatThrownBy(() -> GroupAddress.of(""))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 동일한_주소는_equals를_만족한다() {
		// given
		GroupAddress address1 = GroupAddress.of("서울시 강남구 테헤란로 123", "삼성동 빌딩 1층");
		GroupAddress address2 = GroupAddress.of("서울시 강남구 테헤란로 123", "삼성동 빌딩 1층");

		// when & then
		assertThat(address1).isEqualTo(address2);
		assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
	}

	@Test
	void 다른_주소는_equals를_만족하지_않는다() {
		// given
		GroupAddress address1 = GroupAddress.of("서울시 강남구 테헤란로 123", "삼성동 빌딩 1층");
		GroupAddress address2 = GroupAddress.of("서울시 강남구 테헤란로 456", "삼성동 빌딩 2층");

		// when & then
		assertThat(address1).isNotEqualTo(address2);
	}

	@Test
	void toString은_전체_주소를_반환한다() {
		// given
		String roadAddress = "서울시 강남구 테헤란로 123";
		String detailAddress = "삼성동 빌딩 1층";
		GroupAddress groupAddress = GroupAddress.of(roadAddress, detailAddress);

		// when
		String result = groupAddress.toString();

		// then
		assertThat(result).isEqualTo(roadAddress + " " + detailAddress);
	}
} 