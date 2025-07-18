package com.example.green.domain.challenge.entity.vo;

import static com.example.green.global.utils.EntityValidator.*;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GroupAddress {

	@Column(nullable = false)
	private String roadAddress;

	@Column
	private String detailAddress;

	public static GroupAddress of(String roadAddress, String detailAddress) {
		validateEmptyString(roadAddress, "도로명 주소는 필수 값입니다.");
		return new GroupAddress(roadAddress, detailAddress);
	}

	public static GroupAddress of(String roadAddress) {
		return of(roadAddress, null);
	}

	public String getFullAddress() {
		if (detailAddress != null && !detailAddress.trim().isEmpty()) {
			return roadAddress + " " + detailAddress;
		}
		return roadAddress;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		GroupAddress that = (GroupAddress)obj;
		return Objects.equals(roadAddress, that.roadAddress)
			&& Objects.equals(detailAddress, that.detailAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roadAddress, detailAddress);
	}

	@Override
	public String toString() {
		return getFullAddress();
	}
}
