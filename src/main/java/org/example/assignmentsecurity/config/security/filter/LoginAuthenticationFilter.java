package org.example.assignmentsecurity.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.config.security.dto.LoginReqDto;
import org.example.assignmentsecurity.config.security.dto.LoginRespDto;
import org.example.assignmentsecurity.domain.token.RefreshToken;
import org.example.assignmentsecurity.domain.token.RefreshTokenRepository;
import org.example.assignmentsecurity.domain.user.User;
import org.example.assignmentsecurity.domain.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            JwtProvider jwtProvider,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository
    ) {
        setFilterProcessesUrl("/auth/login");
        this.setAuthenticationManager(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginReqDto loginReqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

            LoginAuthentication loginAuthentication = new LoginAuthentication(
                    loginReqDto.getNickname(),
                    loginReqDto.getPassword()
            );

            return this.getAuthenticationManager().authenticate(loginAuthentication);
        } catch (IOException ex) {
            throw new SecurityFilterChainException(ErrorCode.JSON_PARSE_ERROR, ex);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException {
        String accessToken = jwtProvider.generateAccessToken(authResult);
        String refreshToken = jwtProvider.generateRefreshToken(authResult);
        String replacedToken = accessToken.replace(JwtProvider.TOKEN_PREFIX, "");
        AuthUser authUserForToken = jwtProvider.getAuthUserForToken(replacedToken);
        String nickname = authUserForToken.getNickname();

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new SecurityFilterChainException(ErrorCode.SERVER_ERROR));
        RefreshToken refreshTokenValue = RefreshToken.builder()
                .id(user.getId())
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(JwtProvider.REFRESH_TOKEN_EXPIRATION_TIME))
                .build();
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshTokenValue);

        addCookie(response, savedRefreshToken.getRefreshToken());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(JwtProvider.AUTHENTICATION_HEADER_PREFIX, accessToken);
        response.getWriter().write(objectMapper.writeValueAsString(new LoginRespDto(accessToken)));
    }

    private void addCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(JwtProvider.COOKIE_VALUE_PREFIX, refreshToken.replace(" ", "%20"));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (JwtProvider.REFRESH_TOKEN_EXPIRATION_TIME / 1000));
        response.addCookie(cookie);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResult.error(ErrorCode.INVALID_CREDENTIALS)));
    }
}
