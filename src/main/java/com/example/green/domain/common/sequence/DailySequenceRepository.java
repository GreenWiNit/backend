package com.example.green.domain.common.sequence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailySequenceRepository extends JpaRepository<DailySequence, String> {
}