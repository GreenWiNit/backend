package com.example.green.domain.dashboard.growth.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.entity.Growth;
import com.example.green.domain.dashboard.growth.entity.enums.Level;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.GrowthRepository;
import com.example.green.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GrowthService {

	private final GrowthRepository growthRepository;
	private final GrowthCalculateService calculateService;
	private final MemberRepository memberRepository;

	public LoadGrowthResponse loadGrowth(Long memberId) {

		calculateService.calculateMemberGrowth(memberId);

		memberRepository.findById(memberId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_USER));

		Growth growth = growthRepository.findByMemberId(memberId)
			.orElseGet(() -> {
				Growth newGrowth = Growth.builder()
					.level(Level.SOIL)
					.progress(BigDecimal.ZERO)
					.requiredPoint(BigDecimal.valueOf(250))
					.goalLevel(Level.SPROUT)
					.build();
				return growthRepository.save(newGrowth);
			});

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
