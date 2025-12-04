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

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private String memberName;

	@Column(nullable = false)
	private String profileImageUrl;

	@Column(nullable = false)
	private BigDecimal totalPoint;

	@Column(nullable = false)
	private int certificationCount;

	@Column(nullable = false)
	private int rank;

	@Column(name = "week_start", nullable = false)
	private LocalDate weekStart;

	@Column(name = "week_end", nullable = false)
	private LocalDate weekEnd;

	public void updatePointAndCertification(BigDecimal totalPoint, int certificationCount) {
		this.totalPoint = totalPoint;
		this.certificationCount = certificationCount;
	}

	public void updateRank(int rank) {
		this.rank = rank;
	}

}
