package com.example.green.domain.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.entity.vo.Profile;
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
        Member member = Member.create("google 123456789", "테스트사용자", "test@example.com");

        // when
        MemberStatus status = member.getStatus();

        // then
        assertEquals(MemberStatus.NORMAL, status);
    }

    @Test
    @DisplayName("회원가입시 초기 역할은 USER이다")
    void shouldHaveUserRoleWhenCreated() {
        // given
        Member member = Member.create("google 123456789", "테스트사용자", "test@example.com");

        // when
        MemberRole role = member.getRole();

        // then
        assertEquals(MemberRole.USER, role);
    }

    @Test
    @DisplayName("프로필 이미지가 있는 경우 hasProfileImage()는 true를 반환한다")
    void shouldReturnTrueWhenHasProfileImage() {
        // given
        Profile profileWithImage = new Profile("nickname", "https://example.com/image.jpg");

        // when & then
        assertTrue(profileWithImage.hasProfileImage());
    }

    @Test
    @DisplayName("프로필 이미지가 null이거나 빈 문자열인 경우 hasProfileImage()는 false를 반환한다")
    void shouldReturnFalseWhenNoProfileImage() {
        // given
        Profile profileWithNullImage = new Profile("nickname", null);
        Profile profileWithEmptyImage = new Profile("nickname", "");
        Profile profileWithBlankImage = new Profile("nickname", "   ");

        // when & then
        assertFalse(profileWithNullImage.hasProfileImage());
        assertFalse(profileWithEmptyImage.hasProfileImage());
        assertFalse(profileWithBlankImage.hasProfileImage());
    }

    @Test
    @DisplayName("유효한 닉네임(2-20자)인 경우 isValidNickname()는 true를 반환한다")
    void shouldReturnTrueWhenValidNickname() {
        // given
        Profile profileWith2Chars = new Profile("ab", "image.jpg");
        Profile profileWith20Chars = new Profile("12345678901234567890", "image.jpg");
        Profile profileWith10Chars = new Profile("1234567890", "image.jpg");

        // when & then
        assertTrue(profileWith2Chars.isValidNickname());
        assertTrue(profileWith20Chars.isValidNickname());
        assertTrue(profileWith10Chars.isValidNickname());
    }

    @Test
    @DisplayName("유효하지 않은 닉네임인 경우 isValidNickname()는 false를 반환한다")
    void shouldReturnFalseWhenInvalidNickname() {
        // given
        Profile profileWithNull = new Profile(null, "image.jpg");
        Profile profileWith1Char = new Profile("a", "image.jpg");
        Profile profileWith21Chars = new Profile("123456789012345678901", "image.jpg");

        // when & then
        assertFalse(profileWithNull.isValidNickname());
        assertFalse(profileWith1Char.isValidNickname());
        assertFalse(profileWith21Chars.isValidNickname());
    }
}