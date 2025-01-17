package org.example.assignmentsecurity.controller.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.assignmentsecurity.domain.Role;
import org.example.assignmentsecurity.domain.User;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRespDto {

    private String username;
    private String nickname;
    private List<Role> authorities;

    public UserCreateRespDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = user.getAuthorities();
    }
}
