package com.example.green.domain.dashboard.growth.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.dto.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.GrowthRepository;
import com.example.green.infra.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GrowthService {

	private final GrowthRepository growthRepository;
	private final PointClient pointClient;

	@Transactional
	public void calculateMemberGrowth(Long memberId) {

		Growth growth = growthRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_USER));

		BigDecimal totalPoint = pointClient.getTotalPoints(memberId);

		BigDecimal percent;

		if (totalPoint.compareTo(BigDecimal.ZERO) > 0
			&& totalPoint.compareTo(LevelStandard.LEVEL_2_REQUIREMENT) <= 0) {

			percent = totalPoint
				.divide(LevelStandard.LEVEL_2_REQUIREMENT, 2, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));
			growth.setProgress(Level.SPROUT, percent, LevelStandard.LEVEL_3_REQUIREMENT, Level.SAPLING);
		} else if (totalPoint.compareTo(LevelStandard.LEVEL_2_REQUIREMENT) > 0
			&& totalPoint.compareTo(LevelStandard.LEVEL_3_REQUIREMENT) <= 0) {

			percent = calculatePercentage(LevelStandard.LEVEL_2_REQUIREMENT, LevelStandard.LEVEL_3_REQUIREMENT,
				memberId);

			growth.setProgress(Level.SAPLING, percent, LevelStandard.LEVEL_4_REQUIREMENT, Level.TREE);
		} else if (totalPoint.compareTo(LevelStandard.LEVEL_3_REQUIREMENT) > 0
			&& totalPoint.compareTo(LevelStandard.LEVEL_4_REQUIREMENT) <= 0) {

			percent = calculatePercentage(LevelStandard.LEVEL_3_REQUIREMENT, LevelStandard.LEVEL_4_REQUIREMENT,
				memberId);

			growth.setProgress(Level.TREE, percent, null, Level.TREE);

		} else {
			growth.setProgress(Level.SOIL, BigDecimal.ZERO, LevelStandard.LEVEL_2_REQUIREMENT, Level.SPROUT);
		}
	}

	public LoadGrowthResponse loadGrowth(Long memberId) {
		// Growth 계산 후 업데이트
		calculateMemberGrowth(memberId);

		Growth growth = growthRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_USER));

		Level nextLevel;
		BigDecimal nextLevelPoint;

		switch (growth.getLevel()) {
			case SOIL -> {
				nextLevel = Level.SPROUT;
				nextLevelPoint = LevelStandard.LEVEL_2_REQUIREMENT;
			}
			case SPROUT -> {
				nextLevel = Level.SAPLING;
				nextLevelPoint = LevelStandard.LEVEL_3_REQUIREMENT;
			}
			case SAPLING -> {
				nextLevel = Level.TREE;
				nextLevelPoint = LevelStandard.LEVEL_4_REQUIREMENT;
			}
			case TREE -> {
				nextLevel = Level.TREE;
				nextLevelPoint = null; // 최고 레벨
			}
			default -> throw new GrowthException(GrowthExceptionMessage.INVALID_LEVEL);
		}

		return new LoadGrowthResponse(
			growth.getMember().getId(),
			nextLevel,
			growth.getLevel(),
			growth.getProgress(),
			nextLevelPoint
		);
	}

	public BigDecimal calculatePercentage(BigDecimal currentLevelPoint, BigDecimal nextLevelPoint, Long memberId) {

		BigDecimal totalPoint = pointClient.getTotalPoints(memberId);
		BigDecimal progressLevel = totalPoint.subtract(currentLevelPoint);
		BigDecimal levelRange = nextLevelPoint.subtract(currentLevelPoint);

		return progressLevel
			.divide(levelRange, 2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));
	}
}
