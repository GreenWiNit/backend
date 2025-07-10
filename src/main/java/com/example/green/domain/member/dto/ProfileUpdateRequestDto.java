package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.vo.Profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 업데이트 요청")
public class ProfileUpdateRequestDto {

	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
	@Schema(description = "닉네임", example = "새로운닉네임")
	private String nickname;

	@Schema(description = "프로필 이미지 URL", example = "https://s3.amazonaws.com/bucket/files/profile/uuid.jpg")
	private String profileImageUrl;

	public Profile toProfile() {
		return Profile.builder()
			.nickname(nickname)
			.profileImageUrl(profileImageUrl)
			.build();
	}
} 