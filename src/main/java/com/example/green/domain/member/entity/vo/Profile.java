package com.example.green.domain.member.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

import java.util.Optional;

import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.global.error.exception.BusinessException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"nickname", "profileImageUrl"})
public class Profile {

	private static final int MIN_NICKNAME_LENGTH = 2;
	private static final int MAX_NICKNAME_LENGTH = 20;


	@Column(name = "nickname")
	private String nickname;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Builder
	public Profile(String nickname, String profileImageUrl) {
		validateNickname(nickname);
		this.nickname = nickname.trim();
		this.profileImageUrl = profileImageUrl;
	}


	public Profile update(String newNickname, String newProfileImageUrl) {
		String updatedNickname = Optional.ofNullable(newNickname)
			.map(String::trim)
			.filter(StringUtils::hasText)
			.orElse(this.nickname);

		String updatedImageUrl = Optional.ofNullable(newProfileImageUrl)
			.map(String::trim)
			.filter(StringUtils::hasText)
			.orElse(this.profileImageUrl);

		if (updatedNickname.equals(this.nickname)
			&& updatedImageUrl.equals(this.profileImageUrl)) {
			return this;
		}

		return Profile.builder()
			.nickname(updatedNickname)
			.profileImageUrl(updatedImageUrl)
			.build();
	}


	public boolean hasProfileImage() {
		return StringUtils.hasText(profileImageUrl);
	}

	public boolean isValidNickname() {
		return nickname != null && nickname.length() >= 2 && nickname.length() <= 20;
	}

	private void validateNickname(String nickname) {
		if (!StringUtils.hasText(nickname)) {
			throw new BusinessException(MemberExceptionMessage.MEMBER_NICKNAME_REQUIRED);
		}
		String trimmed = nickname.trim();
		if (trimmed.length() < MIN_NICKNAME_LENGTH|| trimmed.length() > MAX_NICKNAME_LENGTH) {
			throw new BusinessException(MemberExceptionMessage.MEMBER_NICKNAME_INVALID);
		}
	}
}