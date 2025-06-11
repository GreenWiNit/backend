package com.example.green.domain.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {
    NORMAL("NORMAL"),
    DELETED("DELETED")
    ;
    private final String description;
}
