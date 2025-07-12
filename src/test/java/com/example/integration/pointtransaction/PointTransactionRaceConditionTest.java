package com.example.integration.pointtransaction;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.PointTransactionRepository;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.exception.point.PointException;
import com.example.integration.common.BaseIntegrationTest;
import com.example.integration.common.concurrency.ConcurrencyTestResult;
import com.example.integration.common.concurrency.ConcurrencyTestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PointTransactionRaceConditionTest extends BaseIntegrationTest {

	@Autowired
	private PointTransactionService service;

	@Autowired
	private PointTransactionRepository repository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		jdbcTemplate.execute("TRUNCATE TABLE POINT_TRANSACTIONS");
	}

	@Test
	void 잔액이_1000p_일_때_600p_차감이_두번_요청되면_한_건만_처리된다() throws InterruptedException {
		// given
		Long memberId = 1L;
		service.earnPoints(memberId, PointAmount.of(1000), PointSource.ofEvent("초기적립"));

		// when
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(2)
			.timeout(5)
			.execute(() -> service.spendPoints(
				memberId,
				PointAmount.of(600),
				PointSource.ofTarget(1L, "상품구매", TargetType.EXCHANGE)
			));

		// then
		PointAmount pointAmount = service.getPointAmount(memberId);
		assertThat(result.successCount()).isOne();
		assertThat(result.failureCount()).isOne();
		assertThat(pointAmount).isEqualTo(PointAmount.of(400));
	}

	@Test
	void 두_관리자가_동시에_포인트를_지급하면_정확히_합산된다() throws InterruptedException {
		// given
		Long memberId = 1L;

		// when - 두 관리자가 동시에 1000p씩 지급
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(2)
			.timeout(5)
			.testName("관리자 동시 포인트 지급")
			.execute(() -> {
				try {
					service.earnPoints(memberId, PointAmount.of(1000),
						PointSource.ofEvent("관리자지급_" + Thread.currentThread().getName()));
					return true;
				} catch (Exception e) {
					log.error("포인트 지급 실패: {}", e.getMessage());
					return false;
				}
			});

		// then - 둘 다 성공해야 하고, 최종 잔액은 2000p
		assertThat(result.successCount()).isEqualTo(2);
		assertThat(result.failureCount()).isEqualTo(0);

		// 거래 내역도 2건이 정확히 저장되어야 함
		List<PointTransaction> transactions = repository.findAll();
		List<PointTransaction> earnTransactions = transactions.stream()
			.filter(t -> t.getType() == TransactionType.EARN)
			.toList();

		assertThat(earnTransactions).hasSize(2);
		assertThat(earnTransactions)
			.extracting(PointTransaction::getBalanceAfter)
			.containsExactlyInAnyOrder(PointAmount.of(1000), PointAmount.of(2000));
	}

	@Test
	void 관리자_포인트_지급과_사용자_포인트_차감이_동시에_발생하면_정확히_처리된다() throws InterruptedException {
		// given
		Long memberId = 1L;

		// when - 관리자는 1000p 지급, 사용자는 800p 차감 시도
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(2)
			.timeout(10)
			.testName("지급_차감_동시_처리")
			.executeWithParams(
				(Integer taskType) -> {
					try {
						if (taskType == 1) {
							service.earnPoints(memberId, PointAmount.of(1000),
								PointSource.ofEvent("관리자지급"));
						} else {
							service.spendPoints(memberId, PointAmount.of(800),
								PointSource.ofTarget(1L, "상품구매", TargetType.EXCHANGE));
						}
						return true;
					} catch (PointException e) {
						return false;
					}
				},
				Arrays.asList(1, 2)
			);

		// then - 실행 순서에 따라 결과가 달라짐
		assertThat(result.successCount() + result.failureCount()).isEqualTo(2);
		PointAmount finalBalance = service.getPointAmount(memberId);

		// 가능한 시나리오들
		if (result.successCount() == 2) {
			// 둘 다 성공: 지급이 먼저 완료된 경우
			// 1000 (지급) → 700 (차감)
			assertThat(finalBalance).isEqualTo(PointAmount.of(300));
		} else if (result.successCount() == 1) {
			// 하나만 성공: 차감이 먼저 시도되어 실패한 경우
			// 800p 차감 불가, 1000p만 지급됨
			assertThat(finalBalance).isEqualTo(PointAmount.of(1000));
		}
	}
}
