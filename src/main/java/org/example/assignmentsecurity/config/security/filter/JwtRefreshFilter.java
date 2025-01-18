package org.example.assignmentsecurity.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.config.security.dto.RefreshJwtRespDto;
import org.example.assignmentsecurity.domain.token.RefreshToken;
import org.example.assignmentsecurity.domain.token.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtRefreshFilter extends OncePerRequestFilter {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (!request.getRequestURI().equals("/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String refreshToken = extractCookieFormToken(cookies);

        RefreshToken findToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SecurityFilterChainException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (findToken.isExpired()) {
            throw new SecurityFilterChainException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        AuthUser authUserForToken = jwtProvider.getAuthUserForToken(findToken.getRefreshToken().replace(JwtProvider.TOKEN_PREFIX, ""));
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUserForToken);
        String accessToken = jwtProvider.generateAccessToken(loginAuthentication);

        successResponse(response, accessToken);
    }

    private void successResponse(HttpServletResponse response, String accessToken) throws IOException {
        response.setHeader(JwtProvider.AUTHENTICATION_HEADER_PREFIX, accessToken);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResult.success(new RefreshJwtRespDto(accessToken))));
    }

    private String extractCookieFormToken(Cookie[] cookies) {
        return Optional.ofNullable(cookies)
                .map(Arrays::stream)
                .flatMap(stream -> stream
                        .filter(cookie -> cookie.getName().equals(JwtProvider.COOKIE_VALUE_PREFIX))
                        .findFirst()
                        .map(Cookie::getValue)
                        .map(token -> token.replace("%20", " "))
                )
                .orElseThrow(() -> new SecurityFilterChainException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }
}
