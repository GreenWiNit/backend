package com.example.green.infra.database.strategy;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/*
 * DB migration: MySQL -> PostgreSQL.
 * Use {@link org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy}.
 */
@Deprecated(forRemoval = true, since = "x.y")
public class UpperSnakeNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return toUpperCase(super.toPhysicalTableName(name, jdbcEnvironment));
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return toUpperCase(super.toPhysicalColumnName(name, jdbcEnvironment));
	}

	private Identifier toUpperCase(Identifier identifier) {
		if (identifier == null) {
			return null;
		}
		return Identifier.toIdentifier(identifier.getText().toUpperCase(), identifier.isQuoted());
	}
}
