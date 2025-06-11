package com.example.green.domain.member;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile("testNickname", "testProfileImageUrl");
    }

    @Test
    @DisplayName("회원가입시 초기 상태는 NORMAL이다")
    void shouldHaveNormalStatusWhenCreated() {
        // given
        Member member = Member.createNormalMember(profile);

        // when
        MemberStatus status = member.getStatus();

        // then
        assertEquals(MemberStatus.NORMAL, status);
    }

    @Test
    @DisplayName("회원가입시 초기 역할은 USER이다")
    void shouldHaveUserRoleWhenCreated() {
        // given
        Member member = Member.createNormalMember(profile);

        // when
        MemberRole role = member.getRole();

        // then
        assertEquals(MemberRole.USER, role);
    }
}