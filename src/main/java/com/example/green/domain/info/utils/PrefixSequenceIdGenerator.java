package com.example.green.domain.info.utils;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * INFO Id값 커스텀 클래스
 * - P000001
 * */
public class PrefixSequenceIdGenerator implements IdentifierGenerator {

	private static final String PREFIX = "P";
	private static final int NUMBER_LENGTH = 6;

	@Override
	public synchronized Serializable generate(SharedSessionContractImplementor session, Object object)
		throws HibernateException {

		String sql = "SELECT info_id FROM INFO ORDER BY info_id DESC LIMIT 1";
		Object result = session.createNativeQuery(sql).uniqueResult();

		int nextNumber = 1;
		if (result != null) {
			String lastId = result.toString();
			String numberPart = lastId.substring(PREFIX.length());
			nextNumber = Integer.parseInt(numberPart) + 1;
		}

		String format = String.format("%%0%dd", NUMBER_LENGTH);
		return PREFIX + String.format(format, nextNumber);
	}
}
