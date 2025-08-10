// package com.example.green.domain.challengecert.repository;
//
// import static com.example.green.domain.challenge.entity.QTeamChallenge.*;
// import static com.example.green.domain.challenge.entity.QTeamChallengeGroup.*;
// import static com.example.green.domain.challenge.entity.challenge.QTeamChallenge.*;
// import static com.example.green.domain.challenge.entity.challenge.QTeamChallengeParticipation.*;
// import static com.example.green.domain.challenge.entity.group.QGroup.*;
// import static com.example.green.domain.challengecert.entity.QTeamChallengeCertification.*;
// import static com.example.green.domain.challengecert.entity.QTeamChallengeGroupParticipation.*;
// import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;
// import static com.example.green.domain.member.entity.QMember.*;
//
// import java.util.List;
// import java.util.Optional;
//
// import org.springframework.stereotype.Repository;
//
// import com.example.green.domain.challengecert.dto.AdminGroupCodeResponseDto;
// import com.example.green.domain.challengecert.dto.AdminTeamCertificationSearchRequestDto;
// import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
// import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
// import com.example.green.domain.challengecert.entity.CertificationStatus;
// import com.example.green.domain.member.entity.Member;
// import com.example.green.global.api.page.CursorTemplate;
// import com.querydsl.core.types.Projections;
// import com.querydsl.core.types.dsl.BooleanExpression;
// import com.querydsl.jpa.impl.JPAQueryFactory;
//
// import lombok.RequiredArgsConstructor;
//
// @Repository
// @RequiredArgsConstructor
// public class TeamChallengeCertificationRepositoryImpl implements TeamChallengeCertificationRepositoryCustom {
//
// 	private final JPAQueryFactory queryFactory;
//
// 	@Override
// 	public CursorTemplate<Long, ChallengeCertificationListResponseDto> findByMemberWithCursor(
// 		Member member,
// 		Long cursor,
// 		int size
// 	) {
// 		List<TeamChallengeCertification> certifications = queryFactory
// 			.selectFrom(teamChallengeCertification)
// 			.join(teamChallengeCertification.participation, teamChallengeParticipation).fetchJoin()
// 			.join(teamChallengeParticipation.teamChallenge, teamChallenge).fetchJoin()
// 			.where(
// 				teamChallengeParticipation.memberId.eq(member.getId()),
// 				cursorCondition(cursor)
// 			)
// 			.orderBy(teamChallengeCertification.id.desc())
// 			.limit(size + 1)
// 			.fetch();
//
// 		if (certifications.isEmpty()) {
// 			return CursorTemplate.ofEmpty();
// 		}
//
// 		boolean hasNext = certifications.size() > size;
// 		if (hasNext) {
// 			certifications = certifications.subList(0, size);
// 		}
//
// 		List<ChallengeCertificationListResponseDto> dtos = certifications.stream()
// 			.map(this::toCertificationListDto)
// 			.toList();
//
// 		if (hasNext) {
// 			Long nextCursor = certifications.getLast().getId();
// 			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
// 		} else {
// 			return CursorTemplate.of(dtos);
// 		}
// 	}
//
// 	@Override
// 	public Optional<TeamChallengeCertification> findByIdAndMember(Long id, Member member) {
// 		TeamChallengeCertification certification = queryFactory
// 			.selectFrom(teamChallengeCertification)
// 			.join(teamChallengeCertification.participation, teamChallengeParticipation).fetchJoin()
// 			.join(teamChallengeParticipation.teamChallenge, teamChallenge).fetchJoin()
// 			.where(
// 				teamChallengeCertification.id.eq(id),
// 				teamChallengeParticipation.memberId.eq(member.getId())
// 			)
// 			.fetchOne();
//
// 		return Optional.ofNullable(certification);
// 	}
//
// 	private BooleanExpression cursorCondition(Long cursor) {
// 		return cursor != null ? teamChallengeCertification.id.lt(cursor) : null;
// 	}
//
// 	private ChallengeCertificationListResponseDto toCertificationListDto(TeamChallengeCertification certification) {
// 		return ChallengeCertificationListResponseDto.fromTeamChallengeCertification(certification);
// 	}
//
// 	@Override
// 	public List<AdminGroupCodeResponseDto> findGroupCodesByChallengeId(Long challengeId) {
// 		return queryFactory
// 			.select(Projections.constructor(AdminGroupCodeResponseDto.class,
// 				group.teamCode,
// 				group.groupName,
// 				group.currentParticipants
// 			))
// 			.from(teamChallengeGroup)
// 			.where(teamChallengeGroup.teamChallenge.id.eq(challengeId))
// 			.fetch();
// 	}
//
// 	@Override
// 	public CursorTemplate<Long, ChallengeCertificationListResponseDto> findTeamCertificationsWithFilters(
// 		AdminTeamCertificationSearchRequestDto searchRequest, int pageSize) {
//
// 		List<TeamChallengeCertification> certifications = queryFactory
// 			.selectFrom(teamChallengeCertification)
// 			.join(teamChallengeCertification.participation, teamChallengeParticipation).fetchJoin()
// 			.join(teamChallengeParticipation.teamChallenge, teamChallenge).fetchJoin()
// 			.join(teamChallengeParticipation).on(member.id.eq(teamChallengeParticipation.id)).fetchJoin()
// 			.join(teamChallengeParticipation.groupParticipation, teamChallengeGroupParticipation).fetchJoin()
// 			.join(teamChallengeGroupParticipation.teamChallengeGroup, teamChallengeGroup).fetchJoin()
// 			.where(
// 				teamChallengeCondition(searchRequest.challengeId()),
// 				groupCodeCondition(searchRequest.groupCode()),
// 				teamStatusCondition(searchRequest.statuses()),
// 				cursorCondition(searchRequest.cursor())
// 			)
// 			.orderBy(teamChallengeCertification.id.desc())
// 			.limit(pageSize + 1)
// 			.fetch();
//
// 		if (certifications.isEmpty()) {
// 			return CursorTemplate.ofEmpty();
// 		}
//
// 		boolean hasNext = certifications.size() > pageSize;
// 		if (hasNext) {
// 			certifications = certifications.subList(0, pageSize);
// 		}
//
// 		List<ChallengeCertificationListResponseDto> dtos = certifications.stream()
// 			.map(this::toCertificationListDto)
// 			.toList();
//
// 		if (hasNext) {
// 			Long nextCursor = certifications.getLast().getId();
// 			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
// 		} else {
// 			return CursorTemplate.of(dtos);
// 		}
// 	}
//
// 	/**
// 	 * 팀 챌린지 ID 조건
// 	 */
// 	private BooleanExpression teamChallengeCondition(Long challengeId) {
// 		return challengeId != null ? teamChallengeParticipation.teamChallenge.id.eq(challengeId) : null;
// 	}
//
// 	/**
// 	 * 그룹 코드 조건
// 	 */
// 	private BooleanExpression groupCodeCondition(String groupCode) {
// 		return (groupCode != null && !groupCode.trim().isEmpty())
// 			? teamChallengeGroup.teamCode.eq(groupCode.trim()) : null;
// 	}
//
// 	/**
// 	 * 팀 인증 상태 조건
// 	 */
// 	private BooleanExpression teamStatusCondition(List<CertificationStatus> statuses) {
// 		return (statuses != null && !statuses.isEmpty())
// 			? teamChallengeCertification.status.in(statuses) : null;
// 	}
// }
