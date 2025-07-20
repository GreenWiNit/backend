package com.example.green.domain.challengecert.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.BaseChallenge;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationDetailResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.exception.ChallengeCertException;
import com.example.green.domain.challengecert.exception.ChallengeCertExceptionMessage;
import com.example.green.domain.challengecert.repository.PersonalChallengeCertificationRepository;
import com.example.green.domain.challengecert.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeCertificationRepository;
import com.example.green.domain.challengecert.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeCertificationService {

	private static final int DEFAULT_PAGE_SIZE = 20;

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final PersonalChallengeParticipationRepository personalChallengeParticipationRepository;
	private final TeamChallengeParticipationRepository teamChallengeParticipationRepository;
	private final PersonalChallengeCertificationRepository personalChallengeCertificationRepository;
	private final TeamChallengeCertificationRepository teamChallengeCertificationRepository;
	private final MemberRepository memberRepository;
	private final TimeUtils timeUtils;

	/**
	 * 챌린지 인증을 생성합니다.
	 */
	@Transactional
	public ChallengeCertificationCreateResponseDto createCertification(
		Long challengeId,
		ChallengeCertificationCreateRequestDto request,
		PrincipalDetails currentUser
	) {
		// 현재 사용자 조회
		Member member = getMemberById(currentUser.getMemberId());

		// 챌린지 조회
		BaseChallenge challenge = findChallengeById(challengeId);

		// 인증 날짜 검증 (미래 날짜만 차단)
		validateCertificationDate(request.certificationDate());

		// 인증 정보 설정
		LocalDateTime certifiedAt = timeUtils.now(); // 실제 등록 시점
		LocalDate certifiedDate = request.certificationDate(); // 사용자가 선택한 날짜

		// 챌린지 타입에 따라 인증 생성
		if (challenge instanceof PersonalChallenge personalChallenge) {
			return createPersonalChallengeCertification(
				personalChallenge,
				member,
				request.certificationImageUrl(),
				request.certificationReview(),
				certifiedAt,
				certifiedDate
			);
		} else if (challenge instanceof TeamChallenge teamChallenge) {
			return createTeamChallengeCertification(
				teamChallenge,
				member,
				request.certificationImageUrl(),
				request.certificationReview(),
				certifiedAt,
				certifiedDate
			);
		} else {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND);
		}
	}

	/**
	 * 개인 챌린지 인증 목록을 커서 기반으로 조회합니다.
	 */
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> getPersonalChallengeCertifications(
		Long cursor, PrincipalDetails principalDetails) {
		Member member = getMemberById(principalDetails.getMemberId());
		return personalChallengeCertificationRepository.findByMemberWithCursor(member, cursor, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 팀 챌린지 인증 목록을 커서 기반으로 조회합니다.
	 */
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> getTeamChallengeCertifications(
		Long cursor, PrincipalDetails principalDetails) {
		Member member = getMemberById(principalDetails.getMemberId());
		return teamChallengeCertificationRepository.findByMemberWithCursor(member, cursor, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 챌린지 인증 상세 정보를 조회합니다.
	 */
	public ChallengeCertificationDetailResponseDto getChallengeCertificationDetail(
		Long certificationId, PrincipalDetails principalDetails) {
		Member member = getMemberById(principalDetails.getMemberId());

		// 개인 챌린지 인증 먼저 확인
		var personalCertification = personalChallengeCertificationRepository
			.findByIdAndMember(certificationId, member);
		if (personalCertification.isPresent()) {
			return convertToDetailDto(personalCertification.get());
		}

		// 팀 챌린지 인증 확인
		var teamCertification = teamChallengeCertificationRepository
			.findByIdAndMember(certificationId, member);
		if (teamCertification.isPresent()) {
			return convertToDetailDto(teamCertification.get());
		}

		throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_NOT_FOUND);
	}

	private BaseChallenge findChallengeById(Long challengeId) {
		return personalChallengeRepository.findById(challengeId)
			.map(pc -> (BaseChallenge)pc)
			.orElseGet(() -> teamChallengeRepository.findById(challengeId)
				.map(tc -> (BaseChallenge)tc)
				.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND)));
	}

	private Member getMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));
	}

	private ChallengeCertificationCreateResponseDto createPersonalChallengeCertification(
		PersonalChallenge challenge,
		Member member,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		// 참여 정보 조회
		PersonalChallengeParticipation participation = personalChallengeParticipationRepository
			.findByMemberAndPersonalChallenge(member, challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		// 중복 인증 확인 (하루 한 번 제약)
		if (personalChallengeCertificationRepository.existsByParticipationAndCertifiedDate(participation,
			certifiedDate)) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS);
		}

		// 인증 생성
		PersonalChallengeCertification certification = PersonalChallengeCertification.create(
			participation,
			certificationImageUrl,
			certificationReview,
			certifiedAt,
			certifiedDate
		);

		// 인증 저장
		PersonalChallengeCertification savedCertification = personalChallengeCertificationRepository.save(
			certification);

		return ChallengeCertificationCreateResponseDto.builder()
			.certificationId(savedCertification.getId())
			.build();
	}

	private ChallengeCertificationCreateResponseDto createTeamChallengeCertification(
		TeamChallenge challenge,
		Member member,
		String certificationImageUrl,
		String certificationReview,
		LocalDateTime certifiedAt,
		LocalDate certifiedDate
	) {
		// 참여 정보 조회
		TeamChallengeParticipation participation = teamChallengeParticipationRepository
			.findByMemberAndTeamChallenge(member, challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		// 중복 인증 확인 (하루 한 번 제약)
		if (teamChallengeCertificationRepository.existsByParticipationAndCertifiedDate(participation, certifiedDate)) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS);
		}

		// 인증 생성
		TeamChallengeCertification certification = TeamChallengeCertification.create(
			participation,
			certificationImageUrl,
			certificationReview,
			certifiedAt,
			certifiedDate
		);

		// 인증 저장
		TeamChallengeCertification savedCertification = teamChallengeCertificationRepository.save(certification);

		return ChallengeCertificationCreateResponseDto.builder()
			.certificationId(savedCertification.getId())
			.build();
	}

	/**
	 * 인증 날짜 유효성 검증 (미래 날짜만 차단)
	 */
	private void validateCertificationDate(LocalDate requestDate) {
		LocalDate today = timeUtils.now().toLocalDate();

		if (requestDate.isAfter(today)) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.INVALID_CERTIFICATION_DATE);
		}
	}

	/**
	 * 개인 챌린지 인증을 상세용 DTO로 변환
	 */
	private ChallengeCertificationDetailResponseDto convertToDetailDto(
		PersonalChallengeCertification certification) {
		return ChallengeCertificationDetailResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getPersonalChallenge().getId())
			.challengeTitle(certification.getParticipation().getPersonalChallenge().getChallengeName())
			.challengeType(ChallengeType.PERSONAL.name())
			.certificationImageUrl(certification.getCertificationImageUrl())
			.certificationReview(certification.getCertificationReview())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}

	/**
	 * 팀 챌린지 인증을 상세용 DTO로 변환
	 */
	private ChallengeCertificationDetailResponseDto convertToDetailDto(
		TeamChallengeCertification certification) {
		return ChallengeCertificationDetailResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getTeamChallenge().getId())
			.challengeTitle(certification.getParticipation().getTeamChallenge().getChallengeName())
			.challengeType(ChallengeType.TEAM.name())
			.certificationImageUrl(certification.getCertificationImageUrl())
			.certificationReview(certification.getCertificationReview())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}
}
