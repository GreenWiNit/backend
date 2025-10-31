package com.example.green.domain.dashboard.growth.entity;

import java.math.BigDecimal;

import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Growth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private Level level = Level.SOIL;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal progress;

	@Column(nullable = false)
	private BigDecimal requiredPoint;

	@Enumerated(EnumType.STRING)
	private Level goalLevel;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "member_id")
	private Member member;

	public void setProgress(Level level, BigDecimal progress, BigDecimal requiredPoint, Level goalLevel) {
		this.level = (level != null) ? level : this.level;
		this.progress = (progress != null) ? progress : this.progress;
		this.requiredPoint = (requiredPoint != null) ? requiredPoint : this.requiredPoint;
		this.goalLevel = (goalLevel != null) ? goalLevel : this.goalLevel;
	}

}
