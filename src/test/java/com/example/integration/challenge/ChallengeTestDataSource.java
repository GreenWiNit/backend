package com.example.integration.challenge;

import static com.example.green.domain.challenge.entity.challenge.vo.ChallengeType.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.repository.ChallengeRepository;

import jakarta.persistence.EntityManager;

@TestComponent
public class ChallengeTestDataSource {

	@Autowired
	private ChallengeRepository challengeRepository;

	@Autowired
	private EntityManager entityManager;

	public void init() {
		entityManager.createNativeQuery("TRUNCATE TABLE challenge_participations CASCADE").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE TABLE challenges CASCADE").executeUpdate();
		entityManager.createNativeQuery("ALTER SEQUENCE challenges_id_seq RESTART WITH 1").executeUpdate();
		entityManager.createNativeQuery("ALTER SEQUENCE challenge_participations_id_seq RESTART WITH 1")
			.executeUpdate();
	}

	public void 챌린지_50개_생성() {
		List<Challenge> challenges = new ArrayList<>();
		for (int i = 1; i <= 50; i++) {
			ChallengeInfo info = ChallengeInfo.of("challenge" + i, i);
			ChallengeContent content = ChallengeContent.of("content", "https://newImage.url/image.png");
			Challenge challenge = Challenge.of("CODE" + i, info, content, i % 2 == 0 ? PERSONAL : TEAM);
			challenges.add(challenge);
		}
		challengeRepository.saveAllAndFlush(challenges);
	}

	public void 챌린지_참여_역순() {
		for (long id = 50; id >= 1; id--) {
			Challenge challenge = challengeRepository.findByIdWithThrow(id);
			challenge.participate(1L);
			challengeRepository.flush();
		}
	}

	public void 챌린지_하나_참여(Long id, Long memberId) {
		Challenge challenge = challengeRepository.findByIdWithThrow(id);
		challenge.participate(memberId);
		challengeRepository.flush();
	}

	public void 챌린지_미공개(Long id) {
		Challenge challenge = challengeRepository.findByIdWithThrow(id);
		challenge.hide();
		challengeRepository.flush();
	}
}
