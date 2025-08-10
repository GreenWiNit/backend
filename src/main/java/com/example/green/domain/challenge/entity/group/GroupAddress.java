package com.example.green.domain.challenge.entity.group;

import static com.example.green.global.utils.EntityValidator.*;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
public class GroupAddress {

	@Column(nullable = false)
	private String roadAddress;

	@Column
	private String detailAddress;

	private String zoneCode;
	private String sigungu;

	public static GroupAddress of(String roadAddress, String detailAddress, String zoneCode, String sigungu) {
		validateEmptyString(roadAddress, "도로명 주소는 필수 값입니다.");
		return new GroupAddress(roadAddress, detailAddress, zoneCode, sigungu);
	}

	public String getFullAddress() {
		if (detailAddress != null && !detailAddress.trim().isEmpty()) {
			return roadAddress + " " + detailAddress;
		}
		return roadAddress;
	}
}
