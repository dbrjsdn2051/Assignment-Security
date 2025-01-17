package org.example.assignmentsecurity.controller.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.assignmentsecurity.domain.user.User;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRespDto {

    private String username;
    private String nickname;
    private List<UserRoleListRespDto> authorities;

    public UserCreateRespDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = user.getAuthorities().stream().map(UserRoleListRespDto::of).toList();
    }
}
