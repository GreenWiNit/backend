package com.example.green.domain.dashboard.growth.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.dto.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.GrowthRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrowthService {

	private final GrowthRepository growthRepository;
	private final GrowthCalculateService calculateService;

	@Transactional(readOnly = true)
	public LoadGrowthResponse loadGrowth(Long memberId) {

		calculateService.calculateMemberGrowth(memberId);

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

}
