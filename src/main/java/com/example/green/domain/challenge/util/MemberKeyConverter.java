package com.example.green.domain.challenge.util;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.MemberKeySettable;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberKeyConverter {

	private final ClientHelper clientHelper;

	public <T extends MemberKeySettable> void convertSingle(T data) {
		String memberKey = clientHelper.requestMemberKey(data.getMemberId());
		data.setMemberKey(memberKey);
	}

	public <T extends MemberKeySettable> void convert(List<T> data) {
		List<Long> memberIds = data.stream()
			.map(MemberKeySettable::getMemberId)
			.toList();

		Map<Long, String> memberKeyById = clientHelper.requestMemberKeyById(memberIds);
		data.forEach(dto -> dto.setMemberKey(memberKeyById.get(dto.getMemberId())));
	}

	public <T extends MemberKeySettable> void convertPage(PageTemplate<T> page) {
		convert(page.content());
	}
}