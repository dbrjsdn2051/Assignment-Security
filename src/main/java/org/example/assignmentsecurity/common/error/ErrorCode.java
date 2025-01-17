package org.example.assignmentsecurity.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    AUTH_FAIL_ERROR(HttpStatus.UNAUTHORIZED.value(), "인증 및 인가에 실패하였습니다."),
    FILTER_CHAIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "보안 필터 처리 중 오류가 발생했습니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "유효하지 않는 JWT 서명입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "잘못된 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.FORBIDDEN.value(), "만료된 JWT 토큰입니다."),
    ;

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
