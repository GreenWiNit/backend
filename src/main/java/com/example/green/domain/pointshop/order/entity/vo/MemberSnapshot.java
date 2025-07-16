package com.example.green.domain.pointshop.order.entity.vo;

import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSnapshot {

	@Column(nullable = false)
	private Long memberId;
	@Column(nullable = false, updatable = false)
	private String memberCode;

	public MemberSnapshot(Long memberId, String memberCode) {
		validateAutoIncrementId(memberId, "사용자 ID는 필수 값 입니다.");
		validateEmptyString(memberCode, "사용자 코드는 필수 값 입니다.");
		this.memberId = memberId;
		this.memberCode = memberCode;
	}
}
