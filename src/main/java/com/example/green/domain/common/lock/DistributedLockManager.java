package com.example.green.domain.common.lock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockManager {

	private static final int DEFAULT_LOCK_TIMEOUT_SECONDS = 3;

	private final DataSource dataSource;

	public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
		return executeWithLock(lockKey, Duration.ofSeconds(DEFAULT_LOCK_TIMEOUT_SECONDS), supplier);
	}

	public <T> T executeWithLock(String lockKey, Duration timeout, Supplier<T> supplier) {
		try (Connection conn = dataSource.getConnection()) {
			log.debug("임계구역 진입 시도: lockKey={}, connection={}", lockKey, conn);
			return enterCriticalZone(conn, lockKey, timeout, supplier);
		} catch (SQLException e) {
			throw new IllegalStateException("락 처리 중 SQL 오류 발생: lockKey=" + lockKey, e);
		}
	}

	private <T> T enterCriticalZone(
		Connection conn,
		String lockKey,
		Duration timeout,
		Supplier<T> supplier
	) throws SQLException {
		try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT GET_LOCK(?, ?)")) {
			preparedStatement.setString(1, lockKey);
			preparedStatement.setInt(2, (int)timeout.getSeconds());
			validateEnteredLock(preparedStatement.executeQuery(), lockKey, conn);
		}
		return executeImmediately(conn, lockKey, supplier);
	}

	private static void validateEnteredLock(ResultSet resultSet, String lockKey, Connection conn) throws SQLException {
		if (!resultSet.next() || resultSet.getInt(1) == 0) {
			final String exceptionMessage = String.format("임계구역 진입 실패 lockKey=%s, connection=%s", lockKey, conn);
			throw new IllegalStateException(exceptionMessage);
		}
	}

	private <T> T executeImmediately(Connection conn, String lockKey, Supplier<T> supplier) throws SQLException {
		log.debug("임계구역 진입 완료: {} (진행)", lockKey);
		try {
			return supplier.get();
		} finally {
			log.debug("임계구역 종료 시도: lockKey={}, connection={}", lockKey, conn);
			exitCriticalZone(conn, lockKey);
			log.debug("임계구역 종료 완료: {}", lockKey);
		}
	}

	private void exitCriticalZone(Connection conn, String lockKey) throws SQLException {
		try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
			preparedStatement.setString(1, lockKey);
			preparedStatement.executeQuery();
		}
	}
}