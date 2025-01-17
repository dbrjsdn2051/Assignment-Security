package org.example.assignmentsecurity.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getRequestURI().contains("/auth")) {
            filterChain.doFilter(request, response);
        }

        String tokenValue = request.getHeader(JwtProvider.AUTHENTICATION_HEADER_PREFIX);

        if (tokenValue == null) {
            throw new SecurityFilterChainException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String token = tokenValue.replace(JwtProvider.TOKEN_PREFIX, "");
        jwtProvider.validateToken(token);

        AuthUser authUser = jwtProvider.getAuthUserForToken(token);
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUser);
        SecurityContextHolder.getContext().setAuthentication(loginAuthentication);
        filterChain.doFilter(request, response);
    }
}
