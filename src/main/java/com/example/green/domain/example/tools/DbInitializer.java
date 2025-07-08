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
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.repository.PointTransactionRepository;
import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;
import com.example.green.domain.pointshop.entity.order.Order;
import com.example.green.domain.pointshop.entity.order.OrderItem;
import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.ItemSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.MemberSnapshot;
import com.example.green.domain.pointshop.entity.pointproduct.PointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.BasicInfo;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Code;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Media;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Price;
import com.example.green.domain.pointshop.entity.pointproduct.vo.Stock;
import com.example.green.domain.pointshop.repository.DeliveryAddressRepository;
import com.example.green.domain.pointshop.repository.OrderRepository;
import com.example.green.domain.pointshop.repository.PointProductRepository;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@Transactional
@Hidden
public class DbInitializer {

	private static final Random random = new Random();
	private static final LocalDateTime baseTime = LocalDateTime.now();
	private static final Map<String, Integer> lastIds = new HashMap<>();
	private final PointProductRepository pointProductRepository;
	private final PointTransactionRepository pointTransactionRepository;

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

	static String[] targets = {
		"달리기 챌린지", "눈물 나는 챌린지", "기쁜 챌린지", "짜증나는 챌린지", "웃긴 상품", "가짜 상품", "꽝 상품", "랜덤 상품", "1억 상품"
	};

	@GetMapping("/api/tools/init-db-point-transaction")
	public void initPointTransaction() {
		List<PointTransaction> pointTransactions = new ArrayList<>();
		PointAmount currentBalance = PointAmount.of(BigDecimal.ZERO);

		for (int i = 0; i < 100; i++) {
			String target = targets[random.nextInt(targets.length)];
			boolean isChallenge = target.contains("챌린지");

			PointSource pointSource;
			PointAmount amount;
			PointTransaction transaction;

			if (isChallenge) {
				String detail = target + " 적립";
				pointSource = PointSource.ofTarget(random.nextLong(1, 100), detail, TargetType.CHALLENGE);
				int amountPrice = 1000 + random.nextInt(4000);
				amount = PointAmount.of(BigDecimal.valueOf(amountPrice));

				PointAmount previousBalance = currentBalance;
				currentBalance = currentBalance.add(amount);
				transaction = PointTransaction.earn(1L, pointSource, amount, previousBalance);
			} else {
				String detail = target + " 교환";
				pointSource = PointSource.ofTarget(random.nextLong(1, 100), detail, TargetType.EXCHANGE);

				int amountPrice = 1000 + random.nextInt(4000);
				amount = PointAmount.of(BigDecimal.valueOf(amountPrice));

				if (currentBalance.canSpend(amount)) {
					PointAmount previousBalance = currentBalance;
					currentBalance = currentBalance.subtract(amount);
					transaction = PointTransaction.spend(1L, pointSource, amount, previousBalance);
				} else {
					String earnDetail = "보너스 적립";
					PointSource earnSource = PointSource.ofEvent(earnDetail);
					PointAmount earnAmount = PointAmount.of(BigDecimal.valueOf(10000));

					PointAmount previousBalance = currentBalance;
					currentBalance = currentBalance.add(earnAmount);
					transaction = PointTransaction.earn(1L, earnSource, earnAmount, previousBalance);
				}
			}
			pointTransactions.add(transaction);
		}
		pointTransactionRepository.saveAll(pointTransactions);
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

	static String[] firstName = {"김", "이", "박", "최", "강", "구", "오", "정"};
	static String[] secondName = {"길동", "순신", "황", "종서", "호동", "재석", "명수", "재훈", "랜덤"};
	private final DeliveryAddressRepository deliveryAddressRepository;

	@GetMapping("/api/tools/init-db-deliveryAddresses")
	public void initDeliveryAddresses() {
		List<DeliveryAddress> deliveryAddresses = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String name = firstName[random.nextInt(firstName.length)] +
				" " +
				secondName[random.nextInt(secondName.length)];
			String phoneNumber = "010-" + random.nextInt(1000, 10000) + "-" + random.nextInt(1000, 10000);
			Recipient recipient = Recipient.of(name, phoneNumber);
			Address address = Address.of("랜덤 도로명" + i, "상세 주소" + i, String.valueOf(random.nextInt(10000, 100000)));
			DeliveryAddress deliveryAddress = DeliveryAddress.create(i + 1L, recipient, address);
			deliveryAddresses.add(deliveryAddress);
		}
		deliveryAddressRepository.saveAll(deliveryAddresses);
	}

	private final OrderRepository orderRepository;

	@GetMapping("/api/tools/init-db-orders")
	public void initOrders() {
		List<Order> orders = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String name = firstName[random.nextInt(firstName.length)] +
				" " +
				secondName[random.nextInt(secondName.length)];
			String phoneNumber = "010-" + random.nextInt(1000, 10000) + "-" + random.nextInt(1000, 10000);
			DeliveryAddressSnapshot deliveryAddressSnapshot = DeliveryAddressSnapshot.of(
				i + 1L, name, phoneNumber, "랜덤 도로명" + i, "상세 주소" + i,
				String.valueOf(random.nextInt(10000, 100000)));
			MemberSnapshot memberSnapshot = new MemberSnapshot(random.nextLong(1, 101), UUID.randomUUID().toString());
			int price = random.nextInt(1000, 10000);
			ItemSnapshot itemSnapshot =
				new ItemSnapshot(i + 1L, "랜덤 아이템" + i, UUID.randomUUID().toString(), BigDecimal.valueOf(price));
			OrderItem orderItem = OrderItem.create(itemSnapshot, random.nextInt(1, 6));
			Order order = Order.create(deliveryAddressSnapshot, memberSnapshot, List.of(orderItem));
			orders.add(order);
		}
		orderRepository.saveAll(orders);
	}
}
