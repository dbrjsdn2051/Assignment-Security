package org.example.assignmentsecurity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {
    private String nickname;
    private String password;
}
