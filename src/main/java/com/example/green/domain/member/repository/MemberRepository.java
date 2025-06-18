package com.example.green.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
