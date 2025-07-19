package com.example.green.domain.common.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdemPotencyRepository extends JpaRepository<IdemPotency, String> {
}
