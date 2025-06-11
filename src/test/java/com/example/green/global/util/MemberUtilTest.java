package com.example.green.global.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.green.domain.member.Member;
import com.example.green.domain.member.MemberRole;
import com.example.green.domain.member.MemberStatus;
import com.example.green.domain.member.Profile;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.utils.MemberUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MemberUtilTest {

    @Autowired
    private MemberUtil memberUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("현재 로그인한 회원의 전체 정보를 올바르게 반환한다")
    void shouldReturnCompleteCurrentMemberInfo() {
        // given & when
        Member currentMember = memberUtil.getCurrentMember();

        // then
        assertNotNull(currentMember);
        assertEquals(1L, currentMember.getId());
        assertEquals("testNickname", currentMember.getNickname());
        assertEquals("testImageUrl", currentMember.getProfileImageUrl());
        assertEquals(MemberStatus.NORMAL, currentMember.getStatus());
        assertEquals(MemberRole.USER, currentMember.getRole());
        assertNotNull(currentMember.getLastLoginAt());
    }

    @Test
    @DisplayName("getCurrentMember와 getCurrentMemberId는 일관된 ID를 반환한다")
    void shouldReturnConsistentMemberId() {
        // given & when
        Member currentMember = memberUtil.getCurrentMember();
        Long currentMemberId = memberUtil.getCurrentMemberId();

        // then
        assertEquals(currentMember.getId(), currentMemberId);
    }

    @Test
    @DisplayName("회원 Profile 정보가 올바르게 구성된다")
    void shouldReturnCorrectProfileInfo() {
        // given & when
        Member currentMember = memberUtil.getCurrentMember();

        // then
        assertNotNull(currentMember.getProfile());
        assertEquals("testNickname", currentMember.getProfile().nickname());
        assertEquals("testImageUrl", currentMember.getProfile().profileImageUrl());
    }

    @DisplayName("이미 회원이 존재하고 있는 상황에서 임시 회원일 삽입하지 않는다.")
    @Test
    void shouldNotInsertTemporaryMemberWhenMemberAlreadyExists() {
        // given
        Member member = Member.createNormalMember(new Profile("testNickname", "testImageUrl"));
        memberRepository.save(member);

        // when
        memberUtil.getCurrentMember();

        // then
        assertEquals(1, memberRepository.count());
    }

    // TODO: 실제 JWT 구현 후 추가할 테스트들
    /*
    @Test
    @DisplayName("인증되지 않은 사용자는 예외가 발생한다")
    void shouldThrowExceptionWhenNotAuthenticated() {
        // given: 인증되지 않은 상태
        
        // when & then
        assertThrows(BusinessException.class, () -> memberUtil.getCurrentMember());
        assertThrows(BusinessException.class, () -> memberUtil.getCurrentMemberId());
    }

    @Test
    @DisplayName("존재하지 않는 회원 ID의 경우 예외가 발생한다")
    void shouldThrowExceptionWhenMemberNotFound() {
        // given: 존재하지 않는 회원 ID로 인증된 상태
        
        // when & then
        assertThrows(BusinessException.class, () -> memberUtil.getCurrentMember());
    }
    */
} 