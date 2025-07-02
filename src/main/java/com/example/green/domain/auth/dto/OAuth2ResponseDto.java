package com.example.green.domain.auth.dto;

public interface OAuth2ResponseDto {

	//provider (Ex. naver, google,kakao)
	String getProvider();

	//provider에서 발급해주는 아이디
	String getProviderId();

	//이메일
	String getEmail();

	//user 설정한 이름
	String getName();
}
