package org.example.assignmentsecurity.service;

import org.example.assignmentsecurity.common.error.BusinessException;
import org.example.assignmentsecurity.controller.dto.rep.UserCreateRepDto;
import org.example.assignmentsecurity.controller.dto.resp.UserCreateRespDto;
import org.example.assignmentsecurity.controller.dto.resp.UserInfoRespDto;
import org.example.assignmentsecurity.domain.user.Role;
import org.example.assignmentsecurity.domain.user.User;
import org.example.assignmentsecurity.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입시 닉네임이 존재할 경우 예외가 발생한다.")
    void existsNicknameFailTest() {
        // given
        User findUser = User.builder()
                .username("test")
                .nickname("test")
                .password("encodedPassword")
                .build();

        UserCreateRepDto repDto = UserCreateRepDto.builder()
                .nickname("test")
                .username("test user")
                .password("password")
                .build();

        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(repDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.findByNickname(repDto.getNickname())).thenReturn(Optional.of(findUser));

        // when & then
        assertThatThrownBy(() -> userService.register(repDto))
                .isInstanceOf(BusinessException.class);

        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).findByNickname(any());
    }

    @Test
    @DisplayName("정상적으로 회원가입이 수행된다.")
    void registerSuccessTest() {
        // given
        User user = User.builder()
                .username("test")
                .nickname("test user")
                .password("encodedPassword")
                .authorities(List.of(Role.USER))
                .build();

        UserCreateRepDto repDto = UserCreateRepDto.builder()
                .username("test")
                .nickname("test user")
                .password("password")
                .build();

        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(repDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.findByNickname(repDto.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        UserCreateRespDto result = userService.register(repDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo(repDto.getNickname());
        assertThat(result.getUsername()).isEqualTo(repDto.getUsername());
        assertThat(result.getAuthorities().get(0).getAuthorityName()).isEqualTo(Role.USER.getAuthorityName());
    }

    @Test
    @DisplayName("유저 정보 조회 시 해당 유저를 찾을 수 없을때 예외가 발생한다.")
    void notFoundUserFailTest() {
        // given
        String nickname = "test";

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findUser(nickname))
                .isInstanceOf(BusinessException.class);

        verify(userRepository, times(1)).findByNickname(any());
    }

    @Test
    @DisplayName("유저 정보 조회시 정상적으로 조회가 이루어진다.")
    void findUserSuccessTest() {
        // given
        String nickname = "test";

        User findUser = User.builder()
                .username("test")
                .nickname("test")
                .password("encodedPassword")
                .authorities(List.of(Role.USER))
                .build();

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(findUser));

        // when
        UserInfoRespDto result = userService.findUser(nickname);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo(findUser.getNickname());
        assertThat(result.getUsername()).isEqualTo(findUser.getUsername());

        verify(userRepository, times(1)).findByNickname(any());
    }
}