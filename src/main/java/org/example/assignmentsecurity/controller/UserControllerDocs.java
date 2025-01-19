package org.example.assignmentsecurity.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.assignmentsecurity.common.format.ApiResult;
import org.example.assignmentsecurity.config.security.AuthUser;
import org.example.assignmentsecurity.config.security.dto.LoginReqDto;
import org.example.assignmentsecurity.controller.dto.rep.UserCreateRepDto;
import org.example.assignmentsecurity.controller.dto.resp.UserCreateRespDto;
import org.example.assignmentsecurity.controller.dto.resp.UserInfoRespDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Users", description = "사용자 관리 API")
public interface UserControllerDocs {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {
                                                "username": "JIN HO",
                                                "nickname": "Spring",
                                                "authorities": [
                                                  {
                                                    "authorityName": "ROLE_USER"
                                                  }
                                                ]
                                              },
                                              "success": true,
                                              "error": null
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "해당 닉네임을 가진 유저가 이미 존재합니다.",
                                                "status": 400
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "서버 에러가 발생하였습니다.",
                                                "status": 500
                                              }
                                            }
                                            """
                            )))
    })
    @PostMapping("/auth/signup")
    ResponseEntity<ApiResult<UserCreateRespDto>> signin(@RequestBody UserCreateRepDto dto);

    @Operation(summary = "사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {
                                                "username": "JIN HO",
                                                "nickname": "Spring"
                                              },
                                              "success": true,
                                              "error": null
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "사용자 정보 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "해당 유저를 찾을 수 없습니다.",
                                                "status": 404
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "서버 에러가 발생하였습니다.",
                                                "status": 500
                                              }
                                            }
                                            """
                            )
                    ))
    })
    @GetMapping("/api/users")
    ResponseEntity<ApiResult<UserInfoRespDto>> userInfo(@AuthenticationPrincipal AuthUser authUser);

    @Operation(summary = "로그인", description = "사용자 로그인을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {
                                                "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzcHJpbmciLCJSb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImV4cCI6MTczNzg4MDcyOX0.X8HcTgm8Ozon2hMF6-6YA5yDR43kYsTFbKg_UcorLMY"
                                              },
                                              "success": true,
                                              "error": null
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자 정보 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "해당 유저를 찾을 수 없습니다.",
                                                "status": 404
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "잘못된 비밀번호",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "비밀번호가 맞지 않습니다.",
                                                "status": 401
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "서버 에러가 발생하였습니다.",
                                                "status": 500
                                              }
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/auth/login")
    default void login(@RequestBody LoginReqDto loginReqDto){

    }


    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 액세스 토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {
                                                "accessToken": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzcHJpbmciLCJSb2xlIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImV4cCI6MTczNzg4MDg0Mn0.9DIUGvTzFY3vHzmOyvGQXntyC_gp3S9GcIjHEkQuSv0"
                                              },
                                              "success": true,
                                              "error": null
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "리프레쉬 토큰 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "Refresh 토큰을 찾을 수 없습니다.",
                                                "status": 401
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "403", description = "리프레쉬 토큰 만료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "Refresh 토큰이 만료되었습니다.",
                                                "status": 403
                                              }
                                            }
                                            """
                            )
                    )),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "data": {},
                                              "success": false,
                                              "error": {
                                                "message": "서버 에러가 발생하였습니다.",
                                                "status": 500
                                              }
                                            }
                                            """
                            )
                    ))
    })
    @PostMapping("/auth/refresh")
    default void refresh(){

    };

}
