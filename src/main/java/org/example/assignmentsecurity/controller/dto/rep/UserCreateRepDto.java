package org.example.assignmentsecurity.controller.dto.rep;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.assignmentsecurity.domain.user.Role;
import org.example.assignmentsecurity.domain.user.User;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청")
public class UserCreateRepDto {

    @Schema(description = "유저 이름", example = "JIN HO")
    private String username;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "닉네임", example = "Spring")
    private String nickname;

    public static User from(UserCreateRepDto dto, String encodedPassword) {
        return User.builder()
                .nickname(dto.getNickname())
                .username(dto.getUsername())
                .password(encodedPassword)
                .authorities(List.of(Role.USER))
                .build();
    }
}
