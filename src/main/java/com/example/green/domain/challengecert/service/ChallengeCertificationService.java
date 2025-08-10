package com.example.green.domain.challengecert.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.BaseChallenge;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeParticipationRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challengecert.dto.AdminChallengeTitleResponseDto;
import com.example.green.domain.challengecert.dto.AdminGroupCodeResponseDto;
import com.example.green.domain.challengecert.dto.AdminParticipantMemberKeyResponseDto;
import com.example.green.domain.challengecert.dto.AdminPersonalCertificationSearchRequestDto;
import com.example.green.domain.challengecert.dto.AdminTeamCertificationSearchRequestDto;
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
import com.example.green.domain.challengecert.repository.TeamChallengeCertificationRepository;
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
	private static final int ADMIN_PAGE_SIZE = 10;

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
		return personalChallengeCertificationRepository.findByMemberWithCursor(
			principalDetails.getMemberId(), cursor, DEFAULT_PAGE_SIZE);
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
			return ChallengeCertificationDetailResponseDto.fromPersonalChallengeCertification(
				personalCertification.get());
		}

		// 팀 챌린지 인증 확인
		var teamCertification = teamChallengeCertificationRepository
			.findByIdAndMember(certificationId, member);
		if (teamCertification.isPresent()) {
			return ChallengeCertificationDetailResponseDto.fromTeamChallengeCertification(teamCertification.get());
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
			.findByMemberIdAndPersonalChallenge(member.getId(), challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		// 중복 인증 확인 (하루 한 번 제약)
		if (personalChallengeCertificationRepository.existsByParticipationAndCertifiedDate(participation,
			certifiedDate)) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS);
		}

		// 인증 생성
		Member participant = getMemberById(participation.getMemberId());
		PersonalChallengeCertification certification = PersonalChallengeCertification.create(
			participation,
			participant,
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
			.findByMemberIdAndTeamChallenge(member.getId(), challenge)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING));

		// 중복 인증 확인 (하루 한 번 제약)
		if (teamChallengeCertificationRepository.existsByParticipationAndCertifiedDate(participation, certifiedDate)) {
			throw new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_ALREADY_EXISTS);
		}

		// 인증 생성
		Member participant = getMemberById(participation.getMemberId());
		TeamChallengeCertification certification = TeamChallengeCertification.create(
			participation,
			participant,
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

	// ================= 관리자용 메서드들 =================

	/**
	 * 개인 챌린지 제목 목록을 조회합니다. (관리자용)
	 */
	public List<AdminChallengeTitleResponseDto> getPersonalChallengeTitles() {
		List<PersonalChallenge> challenges = personalChallengeRepository.findAll();
		return challenges.stream()
			.map(AdminChallengeTitleResponseDto::from)
			.toList();
	}

	/**
	 * 팀 챌린지 제목 목록을 조회합니다. (관리자용)
	 */
	public List<AdminChallengeTitleResponseDto> getTeamChallengeTitles() {
		List<TeamChallenge> challenges = teamChallengeRepository.findAll();
		return challenges.stream()
			.map(AdminChallengeTitleResponseDto::from)
			.toList();
	}

	/**
	 * 개인 챌린지 참여자 memberKey 목록을 조회합니다. (관리자용)
	 */
	public List<AdminParticipantMemberKeyResponseDto> getPersonalChallengeParticipantMemberKeys(Long challengeId) {
		// 챌린지 존재 여부 확인
		findPersonalChallengeById(challengeId);
		return personalChallengeCertificationRepository.findParticipantMemberKeysByChallengeId(challengeId);
	}

	/**
	 * 팀 챌린지 그룹 코드 목록을 조회합니다. (관리자용)
	 */
	public List<AdminGroupCodeResponseDto> getTeamChallengeGroupCodes(Long challengeId) {
		// 챌린지 존재 여부 확인
		findTeamChallengeById(challengeId);
		return teamChallengeCertificationRepository.findGroupCodesByChallengeId(challengeId);
	}

	/**
	 * 관리자용 개인 챌린지 인증 목록을 복합 조건으로 조회합니다. (커서 기반 페이징)
	 */
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> getPersonalCertificationsWithFilters(
		AdminPersonalCertificationSearchRequestDto searchRequest) {

		// 특정 챌린지가 지정된 경우 존재 여부 확인
		if (!searchRequest.isAllChallenges()) {
			findPersonalChallengeById(searchRequest.challengeId());
		}

		return personalChallengeCertificationRepository.findPersonalCertificationsWithFilters(searchRequest,
			ADMIN_PAGE_SIZE);
	}

	/**
	 * 관리자용 팀 챌린지 인증 목록을 복합 조건으로 조회합니다. (커서 기반 페이징)
	 */
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> getTeamCertificationsWithFilters(
		AdminTeamCertificationSearchRequestDto searchRequest) {

		// 특정 챌린지가 지정된 경우 존재 여부 확인
		if (!searchRequest.isAllChallenges()) {
			findTeamChallengeById(searchRequest.challengeId());
		}

		return teamChallengeCertificationRepository.findTeamCertificationsWithFilters(searchRequest, ADMIN_PAGE_SIZE);
	}

	/**
	 * 인증 상태를 업데이트합니다. (관리자용)
	 */
	@Transactional
	public void updateCertificationStatus(Long certificationId, String status) {
		// 먼저 개인 챌린지 인증에서 찾아보기
		PersonalChallengeCertification personalCertification = personalChallengeCertificationRepository
			.findById(certificationId)
			.orElse(null);

		if (personalCertification != null) {
			updateCertificationStatusInternal(personalCertification, status);
			return;
		}

		// 팀 챌린지 인증에서 찾아보기
		TeamChallengeCertification teamCertification = teamChallengeCertificationRepository
			.findById(certificationId)
			.orElseThrow(() -> new ChallengeCertException(ChallengeCertExceptionMessage.CERTIFICATION_NOT_FOUND));

		updateCertificationStatusInternal(teamCertification, status);
	}

	/**
	 * 인증 상태 업데이트 내부 로직
	 */
	private void updateCertificationStatusInternal(Object certification, String status) {
		switch (status.toUpperCase()) {
			case "PAID" -> approveCertification(certification);
			case "REJECTED" -> rejectCertification(certification);
			default -> throw new ChallengeCertException(ChallengeCertExceptionMessage.INVALID_CERTIFICATION_STATUS);
		}
	}

	/**
	 * 인증을 승인합니다.
	 */
	private void approveCertification(Object certification) {
		if (certification instanceof PersonalChallengeCertification personal) {
			personal.approve();
		} else if (certification instanceof TeamChallengeCertification team) {
			team.approve();
		}
	}

	/**
	 * 인증을 거절합니다.
	 */
	private void rejectCertification(Object certification) {
		if (certification instanceof PersonalChallengeCertification personal) {
			personal.reject();
		} else if (certification instanceof TeamChallengeCertification team) {
			team.reject();
		}
	}

	/**
	 * 개인 챌린지를 ID로 조회합니다.
	 */
	private PersonalChallenge findPersonalChallengeById(Long challengeId) {
		return personalChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND));
	}

	/**
	 * 팀 챌린지를 ID로 조회합니다.
	 */
	private TeamChallenge findTeamChallengeById(Long challengeId) {
		return teamChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND));
	}
}
