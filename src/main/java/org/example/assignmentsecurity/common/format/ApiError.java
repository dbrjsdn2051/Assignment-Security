package org.example.assignmentsecurity.common.format;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

    private final String message;
    private final int status;
}
