package com.example.green.infra.excel.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.example.green.infra.excel.exception.ExcelException;
import com.example.green.infra.excel.exception.ExcelExceptionMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * ExcelDataMapper 구현체들을 관리하는 Registry
 * Spring의 의존성 주입을 통해 모든 ExcelDataMapper 구현체를 자동으로 등록합니다.
 */
@Component
@Slf4j
public class ExcelDataMapperRegistry {

	private final Map<Class<?>, ExcelDataMapper<?>> mapperMap = new ConcurrentHashMap<>();

	public ExcelDataMapperRegistry(List<ExcelDataMapper<?>> mappers) {
		for (ExcelDataMapper<?> mapper : mappers) {
			Class<?> dataType = mapper.getDataType();
			checkDuplicateMapper(mapper, dataType);
			mapperMap.put(dataType, mapper);
			log.info("ExcelDataMapper 등록 완료: {} -> {}",
				dataType.getSimpleName(),
				mapper.getClass().getSimpleName());
		}
	}

	private void checkDuplicateMapper(ExcelDataMapper<?> mapper, Class<?> dataType) {
		if (mapperMap.containsKey(dataType)) {
			String errorMessage = String.format(
				"동일한 데이터 타입을 처리하는 ExcelDataMapper가 중복 등록되었습니다. 데이터 타입: %s, 기존: %s, 중복: %s",
				dataType.getSimpleName(),
				mapperMap.get(dataType).getClass().getSimpleName(),
				mapper.getClass().getSimpleName()
			);
			log.error(errorMessage);
			throw new IllegalStateException(errorMessage);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> ExcelDataMapper<T> getMapper(Class<T> dataType) {
		ExcelDataMapper<?> mapper = mapperMap.get(dataType);

		if (mapper == null) {
			log.error("등록되지 않은 데이터 타입입니다. 타입: {}", dataType.getSimpleName());
			throw new ExcelException(ExcelExceptionMessage.EXCEL_GENERATION_FAILED);
		}

		return (ExcelDataMapper<T>)mapper;
	}
}
