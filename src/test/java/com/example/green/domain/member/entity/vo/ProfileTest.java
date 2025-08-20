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
    @DisplayName("유효한 닉네임으로 Profile을 생성할 수 있다")
    void shouldCreateProfileWithValidNickname() {
        // given & when
        Profile validProfile = new Profile("validNick", "https://example.com/image.jpg");

        // then
        assertThat(validProfile.isValidNickname()).isTrue();
        assertThat(validProfile.getNickname()).isEqualTo("validNick");
    }

    @Test
    @DisplayName("유효하지 않은 닉네임으로 Profile 생성시 예외가 발생한다")
    void shouldThrowExceptionWhenInvalidNickname() {
        // when & then
        assertThatThrownBy(() -> new Profile("a", "https://example.com/image.jpg"))
            .isInstanceOf(Exception.class);
        assertThatThrownBy(() -> new Profile("a".repeat(21), "https://example.com/image.jpg"))
            .isInstanceOf(Exception.class);
        assertThatThrownBy(() -> new Profile(null, "https://example.com/image.jpg"))
            .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("프로필 업데이트 - 닉네임만 변경 (프로필 이미지는 빈 문자열로 유지)")
    void update_OnlyNickname() {
        // given
        Profile original = new Profile("oldNick", "https://example.com/image.jpg");
        String newNickname = "newNick";

        // when
        Profile updated = original.update(newNickname, "");

        // then
        assertThat(updated.getNickname()).isEqualTo(newNickname);
        assertThat(updated.getProfileImageUrl()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("프로필 업데이트 - 프로필 이미지만 변경")
    void update_OnlyProfileImage() {
        // given
        Profile original = new Profile("nickname", "https://example.com/old-image.jpg");
        String newImageUrl = "https://example.com/new-image.jpg";

        // when
        Profile updated = original.update(null, newImageUrl);

        // then
        assertThat(updated.getNickname()).isEqualTo("nickname");
        assertThat(updated.getProfileImageUrl()).isEqualTo(newImageUrl);
    }

    @Test
    @DisplayName("프로필 업데이트 - 닉네임과 프로필 이미지 모두 변경")
    void update_BothNicknameAndImage() {
        // given
        Profile original = new Profile("oldNick", "https://example.com/old-image.jpg");
        String newNickname = "newNick";
        String newImageUrl = "https://example.com/new-image.jpg";

        // when
        Profile updated = original.update(newNickname, newImageUrl);

        // then
        assertThat(updated.getNickname()).isEqualTo(newNickname);
        assertThat(updated.getProfileImageUrl()).isEqualTo(newImageUrl);
    }

    @Test
    @DisplayName("프로필 업데이트 - 변경사항이 없으면 같은 객체 반환")
    void update_NoChanges_ReturnsSameObject() {
        // given
        Profile original = new Profile("nickname", "https://example.com/image.jpg");

        // when
        Profile updated = original.update("nickname", "https://example.com/image.jpg");

        // then
        assertThat(updated).isSameAs(original); // 성능 최적화: 같은 객체 반환
    }

    @Test
    @DisplayName("프로필 업데이트 - 빈 문자열은 무시됨")
    void update_EmptyStringsIgnored() {
        // given
        Profile original = new Profile("nickname", "https://example.com/image.jpg");

        // when
        Profile updated = original.update("   ", "   ");

        // then
        assertThat(updated).isSameAs(original); // 변경사항 없음
        assertThat(updated.getNickname()).isEqualTo("nickname");
        assertThat(updated.getProfileImageUrl()).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    @DisplayName("프로필 업데이트 - 프로필 이미지를 null로 설정하여 기본 이미지로 변경")
    void update_ProfileImageToNull() {
        // given
        Profile original = new Profile("nickname", "https://example.com/image.jpg");

        // when
        Profile updated = original.update(null, null);

        // then
        assertThat(updated.getNickname()).isEqualTo("nickname");
        assertThat(updated.getProfileImageUrl()).isNull();
        assertThat(updated.hasProfileImage()).isFalse();
    }

    @Test
    @DisplayName("프로필 업데이트 - 닉네임 변경하면서 프로필 이미지를 null로 설정")
    void update_NicknameAndProfileImageToNull() {
        // given
        Profile original = new Profile("oldNick", "https://example.com/image.jpg");
        String newNickname = "newNick";

        // when
        Profile updated = original.update(newNickname, null);

        // then
        assertThat(updated.getNickname()).isEqualTo(newNickname);
        assertThat(updated.getProfileImageUrl()).isNull();
        assertThat(updated.hasProfileImage()).isFalse();
    }
}