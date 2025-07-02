package com.example.green.domain.auth.dto;

/**
 * OAuth2 임시 토큰에서 추출한 사용자 정보를 담는 DTO
 */
public class TempTokenInfoDto {

	private final String email;
	private final String name;
	private final String profileImageUrl;
	private final String provider;
	private final String providerId;

	private TempTokenInfoDto(String email, String name, String profileImageUrl, String provider, String providerId) {
		this.email = email;
		this.name = name;
		this.profileImageUrl = profileImageUrl;
		this.provider = provider;
		this.providerId = providerId;
	}

	public static TempTokenInfoBuilder builder() {
		return new TempTokenInfoBuilder();
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getProvider() {
		return provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public static class TempTokenInfoBuilder {
		private String email;
		private String name;
		private String profileImageUrl;
		private String provider;
		private String providerId;

		public TempTokenInfoBuilder email(String email) {
			this.email = email;
			return this;
		}

		public TempTokenInfoBuilder name(String name) {
			this.name = name;
			return this;
		}

		public TempTokenInfoBuilder profileImageUrl(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
			return this;
		}

		public TempTokenInfoBuilder provider(String provider) {
			this.provider = provider;
			return this;
		}

		public TempTokenInfoBuilder providerId(String providerId) {
			this.providerId = providerId;
			return this;
		}

		public TempTokenInfoDto build() {
			return new TempTokenInfoDto(email, name, profileImageUrl, provider, providerId);
		}
	}
} 