package com.example.green.domain.point.entity;

import static com.example.green.global.utils.EntityValidator.*;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TransactionType;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "point_transactions",
	indexes = {
		@Index(name = "idx_member_id_point_transaction_id_desc", columnList = "memberId, pointTransactionId desc")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PointTransaction extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "point_transaction_id")
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	private PointSource pointSource;

	@AttributeOverride(
		name = "amount",
		column = @Column(name = "point_amount", nullable = false, precision = 19, scale = 2)
	)
	private PointAmount pointAmount;
	@AttributeOverride(
		name = "amount",
		column = @Column(name = "balance_after", nullable = false, precision = 19, scale = 2)
	)
	private PointAmount balanceAfter;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	public static PointTransaction earn(
		Long memberId,
		PointSource source,
		PointAmount earnAmount,
		PointAmount currentBalance
	) {
		return buildDefault(memberId, source, earnAmount)
			.balanceAfter(currentBalance.add(earnAmount))
			.type(TransactionType.EARN)
			.build();
	}

	public static PointTransaction spend(
		Long memberId,
		PointSource source,
		PointAmount spendAmount,
		PointAmount currentBalance
	) {
		return buildDefault(memberId, source, spendAmount)
			.balanceAfter(currentBalance.subtract(spendAmount))
			.type(TransactionType.SPEND)
			.build();
	}

	private static PointTransactionBuilder buildDefault(Long memberId, PointSource source, PointAmount pointAmount) {
		validateAutoIncrementId(memberId, "포인트 이력에서 사용자 ID는 필수 값 입니다.");
		validateNullData(source, "포인트 이력 출처는 필수 값 입니다. ");
		validateNullData(pointAmount, "포인트 이력 금액은 필수 값 입니다. ");
		return PointTransaction.builder()
			.memberId(memberId)
			.pointSource(source)
			.pointAmount(pointAmount);
	}
}