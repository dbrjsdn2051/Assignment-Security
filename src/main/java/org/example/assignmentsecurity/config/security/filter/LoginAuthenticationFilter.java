package org.example.assignmentsecurity.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.common.error.SecurityFilterChainException;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.example.assignmentsecurity.config.security.JwtProvider;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.config.security.dto.LoginReqDto;
import org.example.assignmentsecurity.config.security.dto.LoginRespDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    private final JwtProvider jwtProvider;

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, ObjectMapper objectMapper) {
        setFilterProcessesUrl("/auth/login");
        this.setAuthenticationManager(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String token = jwtProvider.generateToken(authResult);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(JwtProvider.AUTHENTICATION_HEADER_PREFIX, token);
        response.getWriter().write(objectMapper.writeValueAsString(new LoginRespDto(token)));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResult.error(ErrorCode.INVALID_CREDENTIALS)));
    }
}
