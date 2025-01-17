package org.example.assignmentsecurity.controller.dto.rep;

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
public class UserCreateRepDto {

    private String username;
    private String password;
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
