package com.example.green.domain.example.tools;

import static com.example.green.domain.pointshop.entity.pointproduct.vo.DisplayStatus.*;
import static com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.repository.PointProductRepository;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Hidden
public class DbInitializer {

	private static final Random random = new Random();
	private static final LocalDateTime baseTime = LocalDateTime.now();
	private static final Map<String, Integer> lastIds = new HashMap<>();
	private final PointProductRepository pointProductRepository;

	@GetMapping("/api/tools/init-db-point-products")
	public void initialize() {
		pointProductRepository.deleteAll();
		List<PointProduct> pointProducts = new ArrayList<>();
		int batchSize = 100;
		for (int i = 0; i < 1000; i++) {
			char ch1 = (char)('A' + random.nextInt(26));
			char ch2 = (char)('A' + random.nextInt(26));
			String prefix = String.format("%c%c", ch1, ch2);
			int number = lastIds.getOrDefault(prefix, 0);
			String code = String.format("PRD-%s-%03d", prefix, number);
			lastIds.put(prefix, number + 1);
			String name = String.format("랜덤 상품 %d", i + 1);
			PointProduct product = createRandomProduct(code, name, i);
			pointProducts.add(product);
			if (pointProducts.size() >= batchSize) {
				List<PointProduct> savedProducts = pointProductRepository.saveAll(pointProducts);

				for (int j = 0; j < savedProducts.size(); j++) {
					PointProduct saved = savedProducts.get(j);
					int dayOffset = i - batchSize + j + 1;
					LocalDateTime randomDate = baseTime.minusDays(dayOffset);
					Field field = ReflectionUtils.findField(BaseEntity.class, "createdDate");
					if (field != null) {
						ReflectionUtils.makeAccessible(field);
						ReflectionUtils.setField(field, saved, randomDate);
					}
				}

				pointProductRepository.saveAll(savedProducts);
				pointProducts.clear();
			}
		}
	}

	private static PointProduct createRandomProduct(String code, String name, int day) {
		return PointProduct.builder()
			.code(new Code(code))
			.basicInfo(new BasicInfo(name, name + " 상품"))
			.media(new Media("https://example.com/images/" + code.toLowerCase() + ".jpg"))
			.price(new Price(BigDecimal.valueOf((random.nextInt(100) + 1) * 1000)))
			.stock(new Stock(random.nextInt(51)))
			.sellingStatus(random.nextInt(10) < 8 ? EXCHANGEABLE : SOLD_OUT)
			.displayStatus(random.nextInt(10) < 8 ? DISPLAY : HIDDEN)
			.build();
	}
}
