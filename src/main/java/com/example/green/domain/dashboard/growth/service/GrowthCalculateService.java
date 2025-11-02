package com.example.green.domain.dashboard.growth.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.GrowthRepository;
import com.example.green.infra.client.PointClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrowthCalculateService {

	private final GrowthRepository growthRepository;
	private final PointClient pointClient;

	@Transactional
	public void calculateMemberGrowth(Long memberId) {

		Growth growth = growthRepository.findByMemberId(memberId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_USER));

		BigDecimal totalPoint = pointClient.getTotalPoints(memberId);
		BigDecimal percent;

		if (totalPoint.compareTo(LevelStandard.LEVEL_2_REQUIREMENT) <= 0) {
			// LEVEL_1 = 0이므로 0~LEVEL_2까지 SOIL 단계
			percent = totalPoint
				.divide(LevelStandard.LEVEL_2_REQUIREMENT, 2, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100));
			growth.setProgress(Level.SOIL, percent, LevelStandard.LEVEL_2_REQUIREMENT, Level.SPROUT);
		} else if (totalPoint.compareTo(LevelStandard.LEVEL_3_REQUIREMENT) <= 0) {
			// LEVEL_2~LEVEL_3까지 SPROUT 단계
			percent = calculatePercentage(LevelStandard.LEVEL_2_REQUIREMENT, LevelStandard.LEVEL_3_REQUIREMENT,
				memberId);
			growth.setProgress(Level.SPROUT, percent, LevelStandard.LEVEL_3_REQUIREMENT, Level.SAPLING);
		} else if (totalPoint.compareTo(LevelStandard.LEVEL_4_REQUIREMENT) <= 0) {
			// LEVEL_3~LEVEL_4까지 SAPLING 단계
			percent = calculatePercentage(LevelStandard.LEVEL_3_REQUIREMENT, LevelStandard.LEVEL_4_REQUIREMENT,
				memberId);
			growth.setProgress(Level.SAPLING, percent, LevelStandard.LEVEL_4_REQUIREMENT, Level.TREE);
		} else {
			// 최고 레벨 이상
			growth.setProgress(Level.TREE, BigDecimal.valueOf(100), null, Level.TREE);
		}
	}

	public BigDecimal calculatePercentage(BigDecimal currentLevelPoint, BigDecimal nextLevelPoint, Long memberId) {

		BigDecimal totalPoint = pointClient.getTotalPoints(memberId);
		BigDecimal progressLevel = totalPoint.subtract(currentLevelPoint);
		BigDecimal levelRange = nextLevelPoint.subtract(currentLevelPoint);

		return progressLevel
			.multiply(BigDecimal.valueOf(100))
			.divide(levelRange, 2, RoundingMode.HALF_UP);
	}
}
