package org.example.assignmentsecurity.controller;

import org.example.assignmentsecurity.common.error.BusinessException;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.LoginAuthentication;
import org.example.assignmentsecurity.controller.dto.resp.UserInfoRespDto;
import org.example.assignmentsecurity.domain.user.Role;
import org.example.assignmentsecurity.domain.user.User;
import org.example.assignmentsecurity.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원 정보 조회 시 회원을 찾을 수 없을 때 예외가 발생한다.")
    void userNotFoundFailTest() throws Exception {
        // given
        LoginAuthentication loginAuthentication = new LoginAuthentication(new AuthUser("test", List.of(Role.USER)));
        SecurityContextHolder.getContext().setAuthentication(loginAuthentication);

        given(userService.findUser(any(String.class)))
                .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value(ErrorCode.USER_NOT_FOUND.getMessage()))
                .andExpect(jsonPath("$.error.status").value(ErrorCode.USER_NOT_FOUND.getStatus()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 조회 시 정상 조회된다.")
    void userInfoSuccessTest() throws Exception {
        // given
        LoginAuthentication loginAuthentication = new LoginAuthentication(new AuthUser("test", List.of(Role.USER)));
        SecurityContextHolder.getContext().setAuthentication(loginAuthentication);

        User findUser = User.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .authorities(List.of(Role.USER))
                .build();

        given(userService.findUser(any(String.class)))
                .willReturn(new UserInfoRespDto(findUser));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(findUser.getUsername()))
                .andExpect(jsonPath("$.data.nickname").value(findUser.getNickname()))
                .andDo(print());
    }

}
