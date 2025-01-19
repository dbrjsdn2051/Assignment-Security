package org.example.assignmentsecurity.config.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청")
public class LoginReqDto {

    @Schema(description = "닉네임", example = "Spring")
    private String nickname;

    @Schema(description = "비밀번호", example = "1234")
    private String password;
}
