package com.example.integration.pointproduct;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.domain.pointshop.item.entity.vo.Category;
import com.example.green.domain.pointshop.product.entity.PointProduct;
import com.example.green.domain.pointshop.product.entity.vo.BasicInfo;
import com.example.green.domain.pointshop.product.entity.vo.Code;
import com.example.green.domain.pointshop.product.entity.vo.Media;
import com.example.green.domain.pointshop.product.entity.vo.Price;
import com.example.green.domain.pointshop.product.entity.vo.Stock;
import com.example.green.domain.pointshop.product.repository.PointProductRepository;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.integration.common.BaseIntegrationTest;
import com.example.integration.common.concurrency.ConcurrencyTestTemplate;

class PointProductConcurrencyTest extends BaseIntegrationTest {

	@Autowired
	private PointProductService pointProductService;

	@Autowired
	private PointProductRepository pointProductRepository;

	private PointProduct product;

	@BeforeEach
	void setUp() {
		pointProductRepository.deleteAllInBatch();

		Stock stock = new Stock(100);
		product = pointProductRepository.saveAndFlush(PointProduct.create(
			new Code("PRD-AA-000"),
			new BasicInfo("name", "desc"),
			new Media("http://thumbnail.com"),
			new Price(BigDecimal.valueOf(1000)),
			stock,
			Category.PRODUCT
		));
	}

	@Test
	@DisplayName("동시에 100개의 재고를 감소시키면 최종 재고는 0이 되어야 한다.")
	void decreaseStock_concurrency_test() throws InterruptedException {
		// when & then
		ConcurrencyTestTemplate.build()
			.threadCount(100)
			.timeout(3)
			.execute(() -> pointProductService.decreaseSingleItemStock(product.getId(), 1));

		PointProduct finalProduct = pointProductRepository.findById(product.getId()).orElseThrow();
		assertThat(finalProduct.getStock().getStock()).isZero();
	}
}
