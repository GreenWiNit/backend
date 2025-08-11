package com.example.green.domain.certification.domain;

import com.example.green.global.utils.EntityValidator;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSnapshot {

	@Column(nullable = false)
	private Long memberId;
	@Column(nullable = false)
	private String memberKey;

	public static MemberSnapshot of(Long id, String key) {
		EntityValidator.validateAutoIncrementId(id, "memberId 필수 값 입니다.");
		EntityValidator.validateEmptyString(key, "memberKey 필수 값 입니다.");
		return new MemberSnapshot(id, key);
	}
}
