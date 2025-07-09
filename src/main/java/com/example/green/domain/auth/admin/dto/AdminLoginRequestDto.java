package com.example.green.domain.auth.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginRequestDto {

	@NotBlank(message = "로그인 ID를 입력해주세요.")
	@Size(max = 50, message = "로그인 ID는 50자 이하로 입력해주세요.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하로 입력해주세요.")
	private String password;
} 