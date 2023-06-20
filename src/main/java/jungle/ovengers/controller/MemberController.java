package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.AuthRequest;
import jungle.ovengers.model.response.MemberResponse;
import jungle.ovengers.model.response.Token;
import jungle.ovengers.service.MemberService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "kakao 로그인 요청")
    @ApiImplicitParam(name = "request", value = "카카오 인증 서버로부터 발급된 인가 코드")
    @PostMapping("/login")
    public ApiResponse<ApiResponse.SuccessBody<Token>> publishToken(@RequestBody AuthRequest request) {
        return ApiResponseGenerator.success(memberService.publishToken(request), HttpStatus.OK);
    }

    @ApiOperation(value = "토큰 재발급 요청")
    @ApiImplicitParam(name = "refreshToken", value = "리프레시 토큰 값")
    @PostMapping("/tokens")
    public ApiResponse<ApiResponse.SuccessBody<Token>> refreshToken(@RequestHeader("X-BBODOK-REFRESH-TOKEN") String refreshToken) {
        return ApiResponseGenerator.success(memberService.reissueTokens(refreshToken), HttpStatus.CREATED);
    }

    @ApiOperation(value = "사용자 탈퇴 요청")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
    })
    @DeleteMapping
    public ApiResponse<ApiResponse.SuccessBody<Void>> delete() {
        memberService.logout();
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED);
    }


    @ApiOperation(value = "사용자 정보 조회 api")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
    })
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<MemberResponse>> read() {
        return ApiResponseGenerator.success(memberService.getUserInfo(), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "사용자가 속해 있는 그룹에서의 정보 조회 api")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
    })
    @GetMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<MemberResponse>> readInGroup(@PathVariable Long groupId) {
        return ApiResponseGenerator.success(memberService.getUserInfoByGroup(groupId), HttpStatus.OK, MessageCode.SUCCESS);
    }
}
