package com.example.green.domain.challenge.entity.challenge.vo;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;
import static com.example.green.global.utils.UriValidator.*;

import com.example.green.domain.challenge.exception.ChallengeException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ChallengeContent {

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private String imageUrl;

	private ChallengeContent(String content, String imageUrl) {
		if (content == null || content.isEmpty()) {
			throw new ChallengeException(CHALLENGE_CONTENT_BLANK);
		}
		if (!isAbsoluteUri(imageUrl)) {
			throw new ChallengeException(INVALID_CHALLENGE_IMAGE);
		}
		this.content = content;
		this.imageUrl = imageUrl;
	}

	public static ChallengeContent of(String content, String imageUrl) {
		return new ChallengeContent(content, imageUrl);
	}
}
