package org.example.assignmentsecurity.common.format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.assignmentsecurity.common.error.ErrorCode;

@Getter
@AllArgsConstructor
public class ApiResult<T> {

    private final T data;
    private final boolean success;
    private final ApiError error;

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data, true, null);
    }

    public static <T> ApiResult<T> error(ErrorCode errorCode) {
        return new ApiResult<>(null, false, new ApiError(errorCode.getMessage(), errorCode.getStatus()));
    }
}
