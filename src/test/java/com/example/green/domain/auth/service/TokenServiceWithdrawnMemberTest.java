package com.example.green.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.service.MemberService;


@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 토큰 생성 실패 문제 해결 테스트")
class TokenServiceWithdrawnMemberTest {

	@Mock
	private MemberService memberService;

	@Test
	@DisplayName("BEFORE: 원래 방식으로는 탈퇴한 사용자를 찾을 수 없었음")
	void shouldNotFindWithdrawnUserWithOriginalApproach() {
		// Given
		String memberKey = "google 123456789";

		given(memberService.findActiveByMemberKey(memberKey))
			.willReturn(Optional.empty());

		// When
		Optional<Member> activeMember = memberService.findActiveByMemberKey(memberKey);

		// Then
		assertThat(activeMember).isEmpty();
	}

	@Test
	@DisplayName("AFTER: 해결된 방식으로는 탈퇴한 사용자를 찾아서 복원 가능")
	void shouldFindAndRestoreWithdrawnUserWithFixedApproach() {
		// Given
		String memberKey = "google 123456789";
		Member withdrawnMember = Member.create(memberKey, "홍길동", "test@example.com");
		withdrawnMember.withdraw();

		given(memberService.findByMemberKey(memberKey))
			.willReturn(Optional.of(withdrawnMember));

		// When
		Optional<Member> foundMember = memberService.findByMemberKey(memberKey);

		// Then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().isWithdrawn()).isTrue();

		Member member = foundMember.get();
		memberService.restoreStatusToNormal(member);

		verify(memberService).restoreStatusToNormal(member);
	}

	@Test
	@DisplayName("복원된 사용자는 정상 상태가 되어야 함")
	void shouldRestoreMemberToNormalStatus() {
		// Given
		Member withdrawnMember = Member.create("google 123456789", "홍길동", "test@example.com");
		withdrawnMember.withdraw();

		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(withdrawnMember.isWithdrawn()).isTrue();

		// When
		withdrawnMember.restoreStatusToNormal();

		// Then
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(withdrawnMember.isWithdrawn()).isFalse();
	}
} 