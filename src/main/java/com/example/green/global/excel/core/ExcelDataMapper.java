package com.example.green.global.excel.core;

import java.util.List;

import com.example.green.global.excel.style.ExcelField;

/**
 * 엑셀 데이터 매핑을 위한 공통 인터페이스
 * 각 도메인에서 이 인터페이스를 구현하여 자신만의 엑셀 포맷을 정의할 수 있습니다.
 *
 * @param <T> 엑셀로 변환할 데이터 타입
 */
public interface ExcelDataMapper<T> {

	/**
	 * 이 Mapper가 처리할 데이터 타입을 반환합니다.
	 * Registry에서 적절한 Mapper를 찾기 위해 사용됩니다.
	 *
	 * @return 처리할 데이터 타입의 Class 객체
	 */
	Class<T> getDataType();

	/**
	 * 엑셀 헤더에 사용될 필드 정보들을 반환합니다.
	 * 필드 순서가 엑셀 컬럼 순서가 됩니다.
	 *
	 * @return 엑셀 필드 정보 리스트 (순서 보장 필요)
	 */
	List<ExcelField> getFields();

	/**
	 * 하나의 데이터 객체에서 엑셀 행 데이터를 추출합니다.
	 * 반환되는 배열의 순서는 getFields()와 일치해야 합니다.
	 *
	 * @param data 엑셀로 변환할 데이터 객체
	 * @return 엑셀 행 데이터 배열 (getFields() 순서와 일치)
	 */
	Object[] extractRowData(T data);

	/**
	 * 엑셀 시트명을 반환합니다.
	 * 기본 구현은 "Sheet1"을 사용하며, 필요에 따라 오버라이드 가능합니다.
	 *
	 * @return 엑셀 시트명
	 */
	default String getSheetName() {
		return "Sheet1";
	}
}