package com.example.green.domain.dashboard.rankingmodule.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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

@Getter
@Entity
@Builder
@Table(
	name = "weekly_ranking",
	uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "week_start"})
)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class WeeklyRanking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String memberName;

	@Column(nullable = false)
	private BigDecimal totalEarned;

	@Column(nullable = false)
	private int certificationCount;

	@Column(nullable = false)
	private String rank;

	@Column(name = "week_start", nullable = false)
	private LocalDate weekStart;

}
