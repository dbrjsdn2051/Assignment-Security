package org.example.assignmentsecurity.controller.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.assignmentsecurity.domain.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoRespDto {
    private String username;
    private String nickname;

    public UserInfoRespDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
