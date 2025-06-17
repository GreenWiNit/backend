package com.example.green.global.excel.core;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelDownloader {

	/**
	 * 주어진 데이터 리스트를 엑셀 파일로 변환하여 OutputStream에 스트리밍합니다.
	 * 타입은 리스트의 첫 번째 요소에서 자동 추론됩니다.
	 *
	 * @param dataList 엑셀로 변환할 데이터 리스트 (비어있으면 안됨)
	 * @param httpServletResponse 엑셀 정보 설정을 위한 HttpServlet 응답
	 * @param <T> 데이터 타입
	 */
	<T> void downloadAsStream(List<T> dataList, HttpServletResponse httpServletResponse);
}
