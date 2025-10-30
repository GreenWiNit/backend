package com.example.green.domain.dashboard.rankingmodule.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.green.domain.common.TimeBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity

@Table(
	name = "weekly_ranking",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "week_start"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class WeeklyRanking extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private String memberName;

	private BigDecimal totalPoint;

	private int certificationCount;

	private int rank;

	@Column(name = "week_start", nullable = false)
	private LocalDate weekStart;

	@Column(name = "week_end", nullable = false)
	private LocalDate weekEnd;

}
