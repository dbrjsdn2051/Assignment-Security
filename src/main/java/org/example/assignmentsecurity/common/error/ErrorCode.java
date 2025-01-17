package org.example.assignmentsecurity.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Security Filter Exception
    AUTH_FAIL_ERROR(HttpStatus.UNAUTHORIZED.value(), "인증 및 인가에 실패하였습니다."),
    FILTER_CHAIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "보안 필터 처리 중 오류가 발생했습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않는 JWT 서명입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "잘못된 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.FORBIDDEN.value(), "만료된 JWT 토큰입니다."),
    JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Json 파싱 에러입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "인증에 실패하였습니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "토큰 정보를 찾을 수 없습니다."),

    // User Domain Exception
    EXISTS_ALREADY_USER(HttpStatus.BAD_REQUEST.value(), "해당 닉네임을 가진 유저가 이미 존재합니다."),
    ;

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
