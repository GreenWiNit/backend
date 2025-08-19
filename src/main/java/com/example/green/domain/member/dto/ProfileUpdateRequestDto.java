package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.vo.Profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 업데이트 요청")
public class ProfileUpdateRequestDto {

	@Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
	@Schema(description = "닉네임 (닉네임 또는 프로필 이미지 중 하나 이상 필수)", example = "새로운닉네임")
	private String nickname;

	@Schema(description = "프로필 이미지 URL", example = "https://s3.amazonaws.com/bucket/files/profile/uuid.jpg", nullable = true)
	private String profileImageUrl;

	@AssertTrue(message = "닉네임 또는 프로필 이미지 중 하나 이상은 필수입니다.")
	private boolean isAtLeastOneFieldPresent() {
		return StringUtils.hasText(nickname) || profileImageUrl != null;
	}
} 