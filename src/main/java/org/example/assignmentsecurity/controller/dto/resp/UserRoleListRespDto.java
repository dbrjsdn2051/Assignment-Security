package org.example.assignmentsecurity.controller.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.assignmentsecurity.domain.user.Role;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleListRespDto {
    private String authorityName;

    public static UserRoleListRespDto of(Role role) {
        return new UserRoleListRespDto(role.getAuthorityName());
    }
}
