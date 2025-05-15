package com.example.green.infra.database.logging;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.internal.FormatStyle;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class CustomP6spySqlFormat implements MessageFormattingStrategy {

	private static final String[] EXCLUDE_PACKAGES = {
		"net.sf.p6spy",
		"com.p6spy",
		"org.springframework",
		"org.hibernate",
		"org.apache",
		"java.lang",
		"java.util",
		"com.sun",
		"com.example.green.infra.database.logging.CustomP6spySqlFormat"
	};
	private static final String INCLUDE_PACKAGE = "com.example.green";
	private static final int MAX_STACK_DEPTH = 3;
	private static final String LOG_TEMPLATE = """
		-----------------------------------
		%s | %d ms | connection %d
		%s
		%s
		-----------------------------------
		""";

	@Override
	public String formatMessage(
		int connectionId,
		String now,
		long elapsed,
		String category,
		String prepared,
		String sql,
		String url
	) {
		if (sql == null || sql.trim().isEmpty()) {
			return "";
		}

		String formattedSql = formatSql(category, sql);
		String callStack = getCallStack();
		return String.format(LOG_TEMPLATE, now, elapsed, connectionId, formattedSql, callStack);
	}

	private static String formatSql(String category, String sql) {
		if (sql == null || sql.trim().isEmpty()) {
			return sql;
		}

		if (!"statement".equals(category)) {
			return sql;
		}

		String trimmedSql = sql.trim().toLowerCase(Locale.ROOT);
		return getSqlWithFormat(sql, trimmedSql);
	}

	private static String getSqlWithFormat(String sql, String trimmedSql) {
		if (isSqlDdl(trimmedSql)) {
			return FormatStyle.DDL.getFormatter().format(sql);
		}
		return FormatStyle.BASIC.getFormatter().format(sql);
	}

	private static boolean isSqlDdl(String trimmedSql) {
		return trimmedSql.startsWith("create")
			|| trimmedSql.startsWith("alter")
			|| trimmedSql.startsWith("comment")
			|| trimmedSql.startsWith("drop");
	}

	private String getCallStack() {
		List<String> traces = findRelevantStackTraces();
		return traces.isEmpty() ? "" : formatCallStack(traces);
	}

	private List<String> findRelevantStackTraces() {
		return Arrays.stream(new Throwable().getStackTrace())
			.map(StackTraceElement::toString)
			.filter(this::isRelevantTrace)
			.limit(MAX_STACK_DEPTH)
			.collect(Collectors.toList());
	}

	private boolean isRelevantTrace(String trace) {
		return !isExcluded(trace) && trace.startsWith(INCLUDE_PACKAGE);
	}

	private String formatCallStack(List<String> traces) {
		StringBuilder sb = new StringBuilder("\nCall Stack:\n");
		for (int i = 0; i < traces.size(); i++) {
			appendTraceEntry(sb, i + 1, traces.get(i));
		}
		return sb.toString();
	}

	private void appendTraceEntry(StringBuilder sb, int index, String trace) {
		sb.append("\t")
			.append(index)
			.append(". ")
			.append(trace)
			.append("\n");
	}

	private static boolean isExcluded(String trace) {
		for (String excludePackage : EXCLUDE_PACKAGES) {
			if (trace.startsWith(excludePackage)) {
				return true;
			}
		}
		return false;
	}
}
