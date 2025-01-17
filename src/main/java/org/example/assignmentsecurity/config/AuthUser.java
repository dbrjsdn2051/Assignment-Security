package org.example.assignmentsecurity.config;

import lombok.Getter;
import org.example.assignmentsecurity.domain.Role;
import org.example.assignmentsecurity.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AuthUser {

    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(String email, List<Role> role) {
        this.email = email;
        this.authorities = role.stream()
                .map(e -> new SimpleGrantedAuthority(e.getAuthorityName())).collect(Collectors.toList());
    }

    public static AuthUser of(User user) {
        return new AuthUser(user.getNickname(), user.getAuthorities());
    }
}