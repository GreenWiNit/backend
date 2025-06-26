package com.example.green.domain.info.utils;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * INFO Id값 커스텀 클래스
 * */
public class PrefixSequenceIdGenerator implements IdentifierGenerator {

	// 접두사와 숫자 자릿수 설정
	private static final String PREFIX = "P";
	private static final int NUMBER_LENGTH = 6;

	@Override
	public synchronized Serializable generate(SharedSessionContractImplementor session, Object object)
		throws HibernateException {

		// 가장 최근에 저장된 ID를 가져오는 네이티브 쿼리
		String sql = "SELECT info_id FROM INFO ORDER BY info_id DESC LIMIT 1";
		Object result = session.createNativeQuery(sql).uniqueResult();

		int nextNumber = 1;
		if (result != null) {
			String lastId = result.toString();
			// PREFIX 제거 후 숫자 부분만 파싱
			String numberPart = lastId.substring(PREFIX.length());
			nextNumber = Integer.parseInt(numberPart) + 1;
		}

		// zero-fill 해서 접두사와 합치기
		String format = String.format("%%0%dd", NUMBER_LENGTH);
		return PREFIX + String.format(format, nextNumber);
	}
}
