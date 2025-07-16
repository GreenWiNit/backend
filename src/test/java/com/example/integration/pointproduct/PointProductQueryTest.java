package com.example.integration.pointproduct;

import static com.example.green.domain.pointshop.product.entity.vo.SellingStatus.*;
import static java.time.LocalDateTime.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.DisplayStatus;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.global.api.page.PageTemplate;
import com.example.integration.common.ServiceIntegrationTest;

import jakarta.persistence.EntityManager;

@Transactional
class PointProductQueryTest extends ServiceIntegrationTest {

	@Autowired
	private PointProductQueryRepository pointProductQueryRepository;
	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	void init() {
		entityManager.clear();
		createProductWithCode("PRD-MA-001", "맥북 M5 Pro", SOLD_OUT, now().minusDays(10));
		createProductWithCode("PRD-TU-001", "어피치 텀블러", EXCHANGEABLE, now().minusDays(9));
		createProductWithCode("PRD-TU-002", "못생긴 텀블러", EXCHANGEABLE, now().minusDays(8));
		createProductWithCode("PRD-TU-003", "귀여운 텀블러", EXCHANGEABLE, now().minusDays(7));
		createProductWithCode("PRD-TU-004", "예쁜 텀블러", SOLD_OUT, now().minusDays(6));
		createProductWithCode("PRD-IP-001", "아이폰 투명 케이스", EXCHANGEABLE, now().minusDays(5));
		createProductWithCode("PRD-IP-002", "아이폰 반짝 케이스", SOLD_OUT, now().minusDays(4));
		createProductWithCode("PRD-GA-001", "갤럭시 S26", EXCHANGEABLE, now().minusDays(3));
		createProductWithCode("PRD-GA-002", "갤럭시 투명 케이스", SOLD_OUT, now().minusDays(2));
		createProductWithCode("PRD-CA-001", "딸기 케이크", EXCHANGEABLE, now().minusDays(1));
		createProductWithCode("PRD-CA-002", "초코 케이크", EXCHANGEABLE, now());
		entityManager.flush();
	}

	/*
	 * pageTemplate 에서 Pagination 으로 인한 원하는 정보가 추출되는 것은 테스트했음.
	 * 쿼리에 의한 totalElements, content 정보만 테스트
	 * */
	@Nested
	class 상품_목록_동적_검색_결과 {

		@Test
		void 키워드로_상품명_검색이_동작한다() {
			// given
			String keyword = "텀블러";
			PointProductSearchCondition condition = getCondition(keyword, null);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			assertThat(res.totalElements()).isEqualTo(4);
			res.content().forEach(pointProd -> assertThat(pointProd.getName()).contains(keyword));
		}

		@Test
		void 키워드로_상품코드_검색이_동작한다() {
			// given
			String keyword = "TU";
			PointProductSearchCondition condition = getCondition(keyword, null);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			assertThat(res.totalElements()).isEqualTo(4);
			res.content().forEach(pointProd -> assertThat(pointProd.getCode()).contains(keyword));
		}

		@ParameterizedTest
		@CsvSource({"EXCHANGEABLE, 7", "SOLD_OUT, 4"})
		void 상태에_따라_동적_검색한다(SellingStatus status, int result) {
			PointProductSearchCondition condition = getCondition(null, status);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			assertThat(res.totalElements()).isEqualTo(result);
			res.content().forEach(pointProd -> assertThat(pointProd.getSellingStatus()).isEqualTo(status));
		}

		@Test
		void 키워드와_상품_상태를_조합해서_검색이_된다() {
			String keyword = "케이스";
			SellingStatus status = EXCHANGEABLE;
			PointProductSearchCondition condition = getCondition(keyword, status);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			assertThat(res.totalElements()).isEqualTo(1);
			res.content().forEach(pointProd -> {
				assertThat(pointProd.getName()).contains(keyword);
				assertThat(pointProd.getSellingStatus()).isEqualTo(status);
			});
		}

		@Test
		void 조건에_맞는_결과가_없으면_빈_결과를_반환한다() {
			// given
			PointProductSearchCondition condition = getCondition("말도 안되는 키워드", EXCHANGEABLE);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			assertThat(res.totalElements()).isZero();
			assertThat(res.content()).isEmpty();
		}

		@Test
		void 조건_없이_조회하면_모두_조회되고_기본적으로_날짜순으로_정렬된다() {
			// given
			PointProductSearchCondition condition = getCondition(null, null);

			// when
			PageTemplate<PointProductSearchResult> res = pointProductQueryRepository.searchPointProducts(condition);

			// then
			List<PointProductSearchResult> copy = new ArrayList<>(res.content());
			copy.sort((r1, r2) -> r2.getCreatedDate().compareTo(r1.getCreatedDate()));
			assertThat(res.totalElements()).isEqualTo(11);
			assertThat(res.content()).hasSize(10);
			assertThat(res.content()).isEqualTo(copy);
		}
	}

	@Nested
	class 엑셀_전용_쿼리_조회 {

		@Test
		void 엑셀_쿼리는_페이지_없이_조회한다() {
			// given
			PointProductExcelCondition condition = new PointProductExcelCondition(null, null);

			// when
			List<PointProductSearchResult> res = pointProductQueryRepository.searchPointProductsForExcel(condition);

			// then
			assertThat(res.size()).isEqualTo(11);
		}
	}

	private static PointProductSearchCondition getCondition(String keyword, SellingStatus status) {
		return new PointProductSearchCondition(null, null, keyword, status);
	}

	private void createProductWithCode(String code, String name, SellingStatus status, LocalDateTime time) {
		PointProduct pointProduct = PointProduct.builder()
			.code(new Code(code))
			.basicInfo(new BasicInfo(name, "테스트 상품"))
			.media(new Media("https://testMedia.com/image.jpg"))
			.price(new Price(BigDecimal.valueOf(1000L)))
			.stock(new Stock(10))
			.sellingStatus(status)
			.displayStatus(DisplayStatus.DISPLAY)
			.build();
		ReflectionTestUtils.setField(pointProduct, "createdDate", time);
		entityManager.persist(pointProduct);
	}
}
