package org.example.assignmentsecurity.security;

import jakarta.servlet.http.Cookie;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.domain.token.RefreshToken;
import org.example.assignmentsecurity.domain.token.RefreshTokenRepository;
import org.example.assignmentsecurity.domain.user.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RefreshTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("accessToken 을 재 발급 요청할 때 정상적으로 재 발급 된다.")
    void refreshTokenSuccessTest() throws Exception {
        // given
        AuthUser authUser = new AuthUser("test", List.of(Role.USER));
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUser);
        String refreshToken = jwtProvider.generateRefreshToken(loginAuthentication);

        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(1000 * 30))
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(token);

        Cookie cookie = new Cookie(JwtProvider.COOKIE_VALUE_PREFIX, savedToken.getRefreshToken());

        // when
        ResultActions result = mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(header().exists(JwtProvider.AUTHENTICATION_HEADER_PREFIX))
                .andDo(print());
    }

    @Test
    @DisplayName("accessToken 재 발급 요청할 때 refreshToken 을 찾을 수 없는 경우 예외가 발생한다.")
    void notFoundRefreshTokenFailTest() throws Exception {
        // given
        AuthUser authUser = new AuthUser("test", List.of(Role.USER));
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUser);
        String refreshToken = jwtProvider.generateRefreshToken(loginAuthentication);
        Cookie cookie = new Cookie(JwtProvider.COOKIE_VALUE_PREFIX, refreshToken.replace(" ", "%20"));

        // when
        ResultActions result = mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("accessToken 재 발급 요청할 때 refreshToken 이 만료된 경우 예외가 발생한다.")
    void expiredRefreshTokenFailTest() throws Exception {
        // given
        AuthUser authUser = new AuthUser("test", List.of(Role.USER));
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUser);
        String refreshToken = jwtProvider.generateRefreshToken(loginAuthentication);

        RefreshToken token = RefreshToken.builder()
                .id(1L)
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().minusSeconds(1000))
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(token);
        Cookie cookie = new Cookie(JwtProvider.COOKIE_VALUE_PREFIX, savedToken.getRefreshToken().replace(" ", "%20"));

        // when
        ResultActions result = mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie));

        // then
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.EXPIRED_REFRESH_TOKEN.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.EXPIRED_JWT_TOKEN.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("accessToken 재 발급 요청했을 때 refreshToken 값을 찾을 수 없을 때 예외가 발생한다.")
    void notFoundRefreshToken() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getStatus()))
                .andDo(print());
    }
}
