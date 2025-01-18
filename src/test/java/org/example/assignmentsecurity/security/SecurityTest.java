package org.example.assignmentsecurity.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.config.security.dto.LoginReqDto;
import org.example.assignmentsecurity.domain.user.Role;
import org.example.assignmentsecurity.domain.user.User;
import org.example.assignmentsecurity.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @Value("${jwt.secret.key}")
    private String secretKey;

    private Key key;
    private SignatureAlgorithm signatureAlgorithm;


    @BeforeEach
    void init() {
        String encodedPassword = passwordEncoder.encode("password");

        user = User.builder()
                .username("test")
                .nickname("test")
                .password(encodedPassword)
                .authorities(List.of(Role.USER))
                .build();

        userRepository.save(user);

        // token init
        byte[] decode = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(decode);
        signatureAlgorithm = SignatureAlgorithm.HS256;
    }

    @AfterEach
    void destroy() {
        userRepository.delete(user);
    }

    @Test
    @DisplayName("로그인 시 nickname 이 맞지 않을 경우 예외가 발생한다.")
    void nicknameMissMatchLoginFailTest() throws Exception {
        // given
        LoginReqDto loginReqDto = new LoginReqDto("fail test", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReqDto)));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.USER_NOT_FOUND.getStatus()))
                .andDo(print())
        ;
    }

    @Test
    @DisplayName("로그인 시 패스워드가 일치하지 않을 경우 예외가 발생한다.")
    void passwordMissMatchLoginFailTest() throws Exception {
        // given
        LoginReqDto loginReqDto = new LoginReqDto("test", "1234");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReqDto)));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.MISS_MATCH_PASSWORD.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.MISS_MATCH_PASSWORD.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인에 성공할 경우 정상적으로 토큰을 발급한다.")
    void loginSuccessTest() throws Exception {
        // given
        LoginReqDto loginReqDto = new LoginReqDto("test", "password");

        // when
        ResultActions result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReqDto)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(cookie().exists(JwtProvider.COOKIE_VALUE_PREFIX))
                .andExpect(header().exists(JwtProvider.AUTHENTICATION_HEADER_PREFIX))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보를 요청할 때 헤더에 토큰값이 없을 경우 예외가 발생한다.")
    void notFoundAccessTokenFailTest() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.TOKEN_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.TOKEN_NOT_FOUND.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보를 요청할 때 토큰값이 정상적이지 않은 경우 예외가 발생한다.")
    void invalidAccessTokenFailTest() throws Exception {
        // given
        String accessToken = "12312asqwer";

        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProvider.AUTHENTICATION_HEADER_PREFIX, accessToken));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.INVALID_JWT_SIGNATURE.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.INVALID_JWT_TOKEN.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보를 요청할 때 토큰 값이 만료되었을 때 예외가 발생한다.")
    void expiredAccessTokenFailTest() throws Exception {
        // given
        String expiredToken = "Bearer " + Jwts.builder().setExpiration(new Date(System.currentTimeMillis()))
                .signWith(key, signatureAlgorithm)
                .compact();

        System.out.println("expiredToken = " + expiredToken);

        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProvider.AUTHENTICATION_HEADER_PREFIX, expiredToken));

        // then
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.EXPIRED_JWT_TOKEN.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.EXPIRED_JWT_TOKEN.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보를 요청할 때 정상적인 토큰값으로 요청한 경우 성공한다.")
    void accessTokenValidSuccessTest() throws Exception {
        // given
        AuthUser authUser = new AuthUser("test", List.of(Role.USER));
        LoginAuthentication loginAuthentication = new LoginAuthentication(authUser);
        String accessToken = jwtProvider.generateAccessToken(loginAuthentication);

        // when
        ResultActions result = mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header(JwtProvider.AUTHENTICATION_HEADER_PREFIX, accessToken));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value(authUser.getNickname()))
                .andDo(print());
    }
}
