package org.example.assignmentsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.example.assignmentsecurity.config.AuthUser;
import org.example.assignmentsecurity.controller.dto.rep.UserCreateRepDto;
import org.example.assignmentsecurity.controller.dto.resp.UserCreateRespDto;
import org.example.assignmentsecurity.controller.dto.resp.UserInfoRespDto;
import org.example.assignmentsecurity.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResult<UserCreateRespDto>> signin(@RequestBody UserCreateRepDto dto) {
        UserCreateRespDto respDto = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success(respDto));
    }

    @GetMapping("/api/users")
    public ResponseEntity<ApiResult<UserInfoRespDto>> userInfo(
            @AuthenticationPrincipal AuthUser authUser
    ) {
        UserInfoRespDto respDto = userService.findUser(authUser.getNickname());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.success(respDto));
    }
}
