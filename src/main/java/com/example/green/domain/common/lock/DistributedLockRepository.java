package com.example.green.domain.common.lock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistributedLockRepository extends JpaRepository<DistributedLock, String> {
}
