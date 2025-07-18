package com.example.green.domain.common.lock;

import java.time.Duration;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "distributed_locks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DistributedLock {

	@Id
	private String lockKey;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	private String owner;

	public static DistributedLock create(String lockKey, LocalDateTime now, Duration timeout, String owner) {
		return new DistributedLock(lockKey, now, now.plus(timeout), owner);
	}
}
