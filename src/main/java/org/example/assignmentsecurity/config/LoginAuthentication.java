package org.example.assignmentsecurity.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class LoginAuthentication implements Authentication {

    private final String nickname;
    private final String password;
    private boolean authenticated;
    private AuthUser principal;

    public LoginAuthentication(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
        this.authenticated = false;
    }

    public LoginAuthentication(AuthUser authUser) {
        this.nickname = authUser.getNickname();
        this.password = null;
        this.authenticated = true;
        this.principal = authUser;
    }

    @Override
    public String getName() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal != null ? principal.getAuthorities() : Collections.emptyList();
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getDetails() {
        return principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }
}
