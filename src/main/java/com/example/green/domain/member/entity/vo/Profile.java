package com.example.green.domain.member.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

import java.util.Objects;
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
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}


	public Profile update(String newNickname, String newProfileImageUrl) {

		String updatedNickname = Optional.ofNullable(newNickname)
			.filter(StringUtils::hasText)
			.orElse(this.nickname);

		String updatedImageUrl;
		if (newProfileImageUrl == null) {
			updatedImageUrl = null;
		} else if (newProfileImageUrl.trim().isEmpty()) {
			updatedImageUrl = this.profileImageUrl;
		} else {
			updatedImageUrl = newProfileImageUrl.trim();
		}
		if (updatedNickname.equals(this.nickname)
			&& Objects.equals(updatedImageUrl, this.profileImageUrl)) {
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
		
		// 공백 검사 (정책: 공백 허용하지 않음 - 앞뒤 및 중간 공백 모두 금지)
		if (nickname.contains(" ") || !nickname.equals(nickname.trim())) {
			throw new BusinessException(MemberExceptionMessage.MEMBER_NICKNAME_INVALID);
		}
		
		// 길이 검사 (정책: 최소 2자, 최대 20자)
		if (nickname.length() < MIN_NICKNAME_LENGTH || nickname.length() > MAX_NICKNAME_LENGTH) {
			throw new BusinessException(MemberExceptionMessage.MEMBER_NICKNAME_INVALID);
		}
		
		// 허용 문자 검사 (정책: 한글, 영문, 숫자만 허용)
		if (!nickname.matches("^[가-힣a-zA-Z0-9]+$")) {
			throw new BusinessException(MemberExceptionMessage.MEMBER_NICKNAME_INVALID);
		}
	}
}