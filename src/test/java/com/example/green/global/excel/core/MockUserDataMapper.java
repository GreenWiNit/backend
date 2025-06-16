package com.example.green.global.excel.core;

import java.util.List;

import com.example.green.global.excel.style.ExcelField;

class MockUserDataMapper implements ExcelDataMapper<MockUser> {

	@Override
	public Class<MockUser> getDataType() {
		return MockUser.class;
	}

	@Override
	public List<ExcelField> getFields() {
		return List.of();
	}

	@Override
	public Object[] extractRowData(MockUser data) {
		return new Object[] {
			data.getName()
		};
	}
}