package com.example.green.domain.member.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"nickname", "profileImageUrl"})
public class Profile {

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	public Profile(String nickname, String profileImageUrl) {
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}

	public boolean hasProfileImage() {
		return profileImageUrl != null && !profileImageUrl.isBlank();
	}

	public boolean isValidNickname() {
		return nickname != null && nickname.length() >= 2 && nickname.length() <= 20;
	}
}
