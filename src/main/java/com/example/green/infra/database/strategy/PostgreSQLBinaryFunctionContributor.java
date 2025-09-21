package com.example.green.infra.database.strategy;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class PostgreSQLBinaryFunctionContributor implements FunctionContributor {

	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
		// BINARY 함수를 PostgreSQL의 COLLATE "C"로 매핑
		functionContributions.getFunctionRegistry().registerPattern(
			"binary",
			"(?1 COLLATE \"C\")",
			functionContributions.getTypeConfiguration()
				.getBasicTypeRegistry()
				.resolve(StandardBasicTypes.STRING)
		);
	}
}