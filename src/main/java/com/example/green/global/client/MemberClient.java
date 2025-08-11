package com.example.green.global.client;

import java.util.List;

public interface MemberClient {

	String getMemberKey(Long memberId);

	List<String> getMemberKeys(List<Long> memberIds);
}
