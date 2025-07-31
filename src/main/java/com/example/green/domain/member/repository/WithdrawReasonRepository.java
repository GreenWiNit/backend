package com.example.green.domain.member.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.member.entity.WithdrawReason;


public interface WithdrawReasonRepository extends JpaRepository<WithdrawReason, Long> {

    Optional<WithdrawReason> findByMemberKey(String memberKey);


} 