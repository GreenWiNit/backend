package com.example.green.domain.auth.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupEventListener {

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;

	/**
	 * 트랜잭션 커밋 후 만료된 토큰 정리
	 *
	 * TODO: 향후 배치 시스템으로 개선 예정
	 * - 현재: 이벤트 리스너에서 즉시 하드 딜리트
	 * - 개선: 소프트 딜리트 + 배치 하드 딜리트
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleTokenCleanup(TokenService.TokenCleanupEvent event) {
		try {
			Member member = memberRepository.findById(event.getMemberId()).orElse(null);
			if (member == null) {
				log.warn("토큰 정리 실패: 멤버를 찾을 수 없음 - ID: {}", event.getMemberId());
				return;
			}
			// TODO: 소프트 딜리트로 개선 예정
			refreshTokenRepository.deleteExpiredAndRevokedTokensByMember(member, LocalDateTime.now());

			log.debug("만료된 토큰 하드 딜리트 완료 - 멤버: {}", event.getMemberUsername());

		} catch (Exception e) {
			// 정리 작업 실패는 메인 플로우에 영향을 주지 않도록 로그만 남김
			log.warn("토큰 정리 작업 실패 (메인 플로우에는 영향 없음) - 멤버: {}, 오류: {}",
				event.getMemberUsername(), e.getMessage());
		}
	}

	/*
	 * TODO: 향후 배치 시스템 구현 시 추가할 기능들
	 *
	 *  배치 완료 이벤트 리스너
	 *  스케줄러 연동
	 *    - @Scheduled: 매일 새벽 2시 실행
	 *    - Spring Batch: 대용량 데이터 처리
	 *    - Quartz: 복잡한 스케줄링 요구사항
	 */
} 