package org.example.assignmentsecurity.domain;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authorityName;

    Role(String authorityName) {
        this.authorityName = authorityName;
    }
}
