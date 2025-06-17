package com.example.green.global.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberUtilTest {

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberUtil memberUtil;

    @Test
    @DisplayName("유효한 회원 ID로 현재 회원 정보를 올바르게 반환한다")
    void shouldReturnCurrentMemberWhenValidId() {
        // given
        Long memberId = 1L;
        Member mockMember = createMockMember();
        
        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        Member result = memberUtil.getCurrentMember();

        // then
        assertNotNull(result);
        assertEquals("testNickname", result.getProfile().getNickname());
        assertEquals("testImageUrl", result.getProfile().getProfileImageUrl());
        assertEquals(MemberStatus.NORMAL, result.getStatus());
        assertEquals(MemberRole.USER, result.getRole());
    }

    @Test
    @DisplayName("현재 회원 ID를 올바르게 반환한다")
    void shouldReturnCurrentMemberId() {
        // given
        Long expectedMemberId = 1L;
        when(securityUtil.getCurrentMemberId()).thenReturn(expectedMemberId);

        // when
        Long result = memberUtil.getCurrentMemberId();

        // then
        assertEquals(expectedMemberId, result);
    }


    @Test
    @DisplayName("회원 Profile 정보가 올바르게 구성된다")
    void shouldReturnCorrectProfileInfo() {
        // given
        Long memberId = 1L;
        Member mockMember = createMockMember();
        
        when(securityUtil.getCurrentMemberId()).thenReturn(memberId);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(mockMember));

        // when
        Member result = memberUtil.getCurrentMember();

        // then
        assertNotNull(result.getProfile());
        assertEquals("testNickname", result.getProfile().getNickname());
        assertEquals("testImageUrl", result.getProfile().getProfileImageUrl());
    }

    private Member createMockMember() {
        Profile profile = new Profile("testNickname", "testImageUrl");
        return Member.createNormalMember(profile);
    }
} 