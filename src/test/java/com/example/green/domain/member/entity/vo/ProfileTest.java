package com.example.green.domain.member.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    @DisplayName("같은 닉네임과 프로필 이미지 URL을 가진 Profile 객체는 동등하다")
    void shouldBeEqualWhenSameNicknameAndProfileImageUrl() {
        // given
        String nickname = "testUser";
        String profileImageUrl = "https://example.com/image.jpg";
        
        Profile profile1 = new Profile(nickname, profileImageUrl);
        Profile profile2 = new Profile(nickname, profileImageUrl);
        
        // when & then
        assertThat(profile1).isEqualTo(profile2);
        assertThat(profile1.hashCode()).isEqualTo(profile2.hashCode());
    }

    @Test
    @DisplayName("다른 닉네임을 가진 Profile 객체는 동등하지 않다")
    void shouldNotBeEqualWhenDifferentNickname() {
        // given
        Profile profile1 = new Profile("user1", "https://example.com/image.jpg");
        Profile profile2 = new Profile("user2", "https://example.com/image.jpg");
        
        // when & then
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    @DisplayName("다른 프로필 이미지 URL을 가진 Profile 객체는 동등하지 않다")
    void shouldNotBeEqualWhenDifferentProfileImageUrl() {
        // given
        Profile profile1 = new Profile("testUser", "https://example.com/image1.jpg");
        Profile profile2 = new Profile("testUser", "https://example.com/image2.jpg");
        
        // when & then
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    @DisplayName("null 값과 비교시 동등하지 않다")
    void shouldNotBeEqualWhenComparedWithNull() {
        // given
        Profile profile = new Profile("testUser", "https://example.com/image.jpg");
        
        // when & then
        assertThat(profile).isNotEqualTo(null);
    }

    @Test
    @DisplayName("자기 자신과는 동등하다")
    void shouldBeEqualWithItself() {
        // given
        Profile profile = new Profile("testUser", "https://example.com/image.jpg");
        
        // when & then
        assertThat(profile).isEqualTo(profile);
    }

    @Test
    @DisplayName("프로필 이미지가 있는지 확인할 수 있다")
    void shouldCheckIfHasProfileImage() {
        // given
        Profile profileWithImage = new Profile("user", "https://example.com/image.jpg");
        Profile profileWithoutImage = new Profile("user", null);
        Profile profileWithBlankImage = new Profile("user", "   ");
        
        // when & then
        assertThat(profileWithImage.hasProfileImage()).isTrue();
        assertThat(profileWithoutImage.hasProfileImage()).isFalse();
        assertThat(profileWithBlankImage.hasProfileImage()).isFalse();
    }

    @Test
    @DisplayName("닉네임 유효성을 검증할 수 있다")
    void shouldValidateNickname() {
        // given
        Profile validProfile = new Profile("validNick", "https://example.com/image.jpg");
        Profile tooShortProfile = new Profile("a", "https://example.com/image.jpg");
        Profile tooLongProfile = new Profile("a".repeat(21), "https://example.com/image.jpg");
        Profile nullNicknameProfile = new Profile(null, "https://example.com/image.jpg");
        
        // when & then
        assertThat(validProfile.isValidNickname()).isTrue();
        assertThat(tooShortProfile.isValidNickname()).isFalse();
        assertThat(tooLongProfile.isValidNickname()).isFalse();
        assertThat(nullNicknameProfile.isValidNickname()).isFalse();
    }
} 