package org.example.assignmentsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.assignmentsecurity.common.error.BusinessException;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.controller.dto.rep.UserCreateRepDto;
import org.example.assignmentsecurity.controller.dto.resp.UserCreateRespDto;
import org.example.assignmentsecurity.domain.user.Role;
import org.example.assignmentsecurity.domain.user.User;
import org.example.assignmentsecurity.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserRegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원 가입 시 중복된 회원일 경우 예외가 발생한다.")
    void existsUserFailTest() throws Exception {
        // given
        UserCreateRepDto repDto = UserCreateRepDto.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .build();

        given(userService.register(any(UserCreateRepDto.class)))
                .willThrow(new BusinessException(ErrorCode.EXISTS_ALREADY_USER));

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 가입이 장상적으로 이루어진다.")
    void registerSuccessTest() throws Exception {
        // given
        UserCreateRepDto repDto = UserCreateRepDto.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .build();

        User user = User.builder()
                .username("test")
                .nickname("test")
                .authorities(List.of(Role.USER))
                .build();

        UserCreateRespDto respDto = new UserCreateRespDto(user);

        given(userService.register(any(UserCreateRepDto.class)))
                .willReturn(respDto);

        // when & then
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(repDto.getUsername()))
                .andDo(print());
    }
}