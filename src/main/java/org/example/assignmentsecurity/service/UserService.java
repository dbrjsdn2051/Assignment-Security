package org.example.assignmentsecurity.service;

import lombok.RequiredArgsConstructor;
import org.example.assignmentsecurity.common.error.BusinessException;
import org.example.assignmentsecurity.common.error.ErrorCode;
import org.example.assignmentsecurity.controller.dto.rep.UserCreateRepDto;
import org.example.assignmentsecurity.controller.dto.resp.UserCreateRespDto;
import org.example.assignmentsecurity.controller.dto.resp.UserInfoRespDto;
import org.example.assignmentsecurity.domain.User;
import org.example.assignmentsecurity.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserCreateRespDto register(UserCreateRepDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        userRepository.findByNickname(dto.getNickname())
                .ifPresent(user -> {
                    throw new BusinessException(ErrorCode.EXISTS_ALREADY_USER);
                });

        User user = UserCreateRepDto.from(dto, encodedPassword);
        User savedUser = userRepository.save(user);
        return new UserCreateRespDto(savedUser);
    }

    public UserInfoRespDto findUser(String nickname) {
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new UserInfoRespDto(findUser);
    }
}
