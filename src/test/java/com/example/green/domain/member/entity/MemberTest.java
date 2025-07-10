package com.example.green.domain.member.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.green.domain.member.entity.enums.MemberRole;
import com.example.green.domain.member.entity.enums.MemberStatus;

class MemberTest {

	@Test
	@DisplayName("회원을 생성하면 기본 상태는 NORMAL이다")
	void createMember_ShouldHaveNormalStatus() {
		// given
		String username = "google 123456789";
		String name = "홍길동";
		String email = "hong@example.com";

		// when
		Member member = Member.create(username, name, email);

		// then
		assertThat(member.getUsername()).isEqualTo(username);
		assertThat(member.getName()).isEqualTo(name);
		assertThat(member.getEmail()).isEqualTo(email);
		assertThat(member.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(member.getRole()).isEqualTo(MemberRole.USER);
		assertThat(member.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("회원 탈퇴 시 상태가 DELETED로 변경되고 soft delete 플래그가 설정된다")
	void withdraw_ShouldMarkMemberAsDeleted() {
		// given
		Member member = Member.create("google 123", "테스트", "test@example.com");

		// when
		member.withdraw();

		// then
		assertThat(member.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(member.isDeleted()).isTrue();
		assertThat(member.getLastLoginAt()).isNull();
	}

	@Test
	@DisplayName("정상 회원은 활성 상태로 판별된다")
	void normalMember_ShouldBeActive() {
		// given
		Member member = Member.create("google 123", "테스트", "test@example.com");

		// when & then
		assertThat(member.isWithdrawn()).isFalse();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(member.isDeleted()).isFalse();
	}

	@Test
	@DisplayName("탈퇴한 회원은 탈퇴 상태로 판별된다")
	void withdrawnMember_ShouldBeWithdrawn() {
		// given
		Member member = Member.create("google 123", "테스트", "test@example.com");

		// when
		member.withdraw();

		// then
		assertThat(member.isWithdrawn()).isTrue();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(member.isDeleted()).isTrue();
	}

	@Test
	@DisplayName("soft delete만 된 회원도 탈퇴 상태로 판별된다")
	void softDeletedMember_ShouldBeWithdrawn() {
		// given
		Member member = Member.create("google 123", "테스트", "test@example.com");

		// when
		member.markDeleted(); // BaseEntity의 soft delete만 적용

		// then
		assertThat(member.isWithdrawn()).isTrue();
		assertThat(member.getStatus()).isEqualTo(MemberStatus.NORMAL); // 상태는 여전히 NORMAL
		assertThat(member.isDeleted()).isTrue(); // 하지만 soft delete됨
	}

	@Test
	@DisplayName("프로필 업데이트가 정상적으로 동작한다")
	void updateProfile_ShouldUpdateProfileInfo() {
		// given
		Member member = Member.create("google 123", "테스트", "test@example.com");
		String newNickname = "새로운닉네임";
		String newProfileImageUrl = "https://example.com/new-profile.jpg";

		// when
		member.updateProfile(newNickname, newProfileImageUrl);

		// then
		assertThat(member.getProfile().getNickname()).isEqualTo(newNickname);
		assertThat(member.getProfile().getProfileImageUrl()).isEqualTo(newProfileImageUrl);
	}

	@Test
	@DisplayName("OAuth2 정보 업데이트가 정상적으로 동작한다")
	void updateOAuth2Info_ShouldUpdateNameAndEmail() {
		// given
		Member member = Member.create("google 123", "원래이름", "old@example.com");
		String newName = "새로운이름";
		String newEmail = "new@example.com";

		// when
		member.updateOAuth2Info(newName, newEmail);

		// then
		assertThat(member.getName()).isEqualTo(newName);
		assertThat(member.getEmail()).isEqualTo(newEmail);
		assertThat(member.getLastLoginAt()).isNotNull();
	}
} 