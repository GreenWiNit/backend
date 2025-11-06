package com.example.green.domain.pointshop.item.excel;

import com.example.green.infra.excel.style.BackGroundColor;
import com.example.green.infra.excel.style.FieldFormat;

public enum PointItemExcelStyle {

	CODE("아이템 코드", BackGroundColor.LIGHT_GRAY, FieldFormat.TEXT),
	NAME("아이템명", BackGroundColor.LIGHT_GRAY, FieldFormat.TEXT),
	POINT("교환 포인트", BackGroundColor.LIGHT_GRAY, FieldFormat.POINT),
	DISPLAY("전시 여부", BackGroundColor.LIGHT_GRAY, FieldFormat.TEXT),
	CREATED_DATE("등록일", BackGroundColor.LIGHT_GRAY, FieldFormat.DATE);

	private final String header;
	private final BackGroundColor bgColor;
	private final FieldFormat format;

	PointItemExcelStyle(String header, BackGroundColor bgColor, FieldFormat format) {
		this.header = header;
		this.bgColor = bgColor;
		this.format = format;
	}

	public String getHeader() {
		return header;
	}

	public BackGroundColor getBgColor() {
		return bgColor;
	}

	public FieldFormat getFormat() {
		return format;
	}
}

