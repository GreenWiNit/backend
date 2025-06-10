package com.example.green.domain.user.member;

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
