package com.example.green.domain.member.repository.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "사용자 포인트 조회 응답")
@AllArgsConstructor
public class MemberPointsDto {

	@Schema(description = "사용자 식별자", example = "1")
	private Long memberId;
	@Schema(description = "사용자 이메일", example = "greenWinit@email.com")
	private String memberEmail;
	@Schema(description = "사용자 닉네임", example = "그린위닛")
	private String memberNickname;
	@Schema(description = "사용자 포인트", example = "1")
	private BigDecimal memberPoint;

	public MemberPointsDto(Long id, String email, String name) {
		this.memberId = id;
		this.memberEmail = email;
		this.memberNickname = name;
	}
}
