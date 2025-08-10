package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.common.service.FileManager;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

	private final FileManager fileManager;
	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final TimeUtils timeUtils;

	public Long createPersonalChallenge(AdminChallengeCreateDto request) {
		long count = personalChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generatePersonalCode(timeUtils.now(), count + 1);
		PersonalChallenge challenge = PersonalChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		PersonalChallenge savedChallenge = personalChallengeRepository.save(challenge);
		fileManager.confirmUsingImage(savedChallenge.getChallengeImage());
		
		return savedChallenge.getId();
	}

	public Long createTeamChallenge(AdminChallengeCreateDto request) {
		long count = teamChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generateTeamCode(timeUtils.now(), count + 1);
		TeamChallenge challenge = TeamChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		TeamChallenge saved = teamChallengeRepository.save(challenge);
		return saved.getId();
	}

	public void joinPersonalChallenge(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.addParticipation(memberId, timeUtils.now());
	}

	public void joinTeamChallenge(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.addParticipation(memberId, timeUtils.now());
	}

	public void leavePersonalChallenge(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.removeParticipation(memberId, timeUtils.now());
	}

	public void leaveTeamChallenge(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.removeParticipation(memberId, timeUtils.now());
		// todo: 그룹 탈퇴 처리 필요
	}

	public void showTeamChallenge(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.show();
	}

	public void hideTeamChallenge(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.hide();
	}

	public void showPersonalChallenge(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.show();
	}

	public void hidePersonalChallenge(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.hide();
	}

	public void updateTeamChallenge(Long challengeId, AdminChallengeUpdateDto dto) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.updateBasicInfo(
			dto.challengeName(), dto.challengePoint(), dto.beginDateTime(), dto.endDateTime(), dto.challengeContent()
		);
		fileManager.unUseImage(teamChallenge.getChallengeImage());
		teamChallenge.updateImage(dto.challengeImageUrl());
		fileManager.confirmUsingImage(teamChallenge.getChallengeImage());
	}

	public void updatePersonalChallenge(Long challengeId, AdminChallengeUpdateDto dto) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.updateBasicInfo(
			dto.challengeName(), dto.challengePoint(), dto.beginDateTime(), dto.endDateTime(), dto.challengeContent()
		);
		fileManager.unUseImage(personalChallenge.getChallengeImage());
		personalChallenge.updateImage(dto.challengeImageUrl());
		fileManager.confirmUsingImage(personalChallenge.getChallengeImage());
	}
}
