package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.AuthRequest;
import jungle.ovengers.model.request.StudyHistoryRequest;
import jungle.ovengers.model.response.StudyHistoryResponse;
import jungle.ovengers.model.response.Token;
import jungle.ovengers.service.MemberService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @ApiOperation(value = "kakao 인증 api")
    @ApiImplicitParam(name = "request", value = "카카오 인증 서버로부터 발급된 인가 코드")
    @PostMapping("/tokens")
    public ApiResponse<ApiResponse.SuccessBody<Token>> publishToken(@RequestBody AuthRequest request) {
        return ApiResponseGenerator.success(memberService.publishToken(request), HttpStatus.OK);
    }
    @ApiOperation(value = "학습 날짜, 시간 조회 - 마이페이지 잔디")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "request", value = "조회 하려는 기간 from, to")
    })
    @GetMapping("/history")
    public ApiResponse<ApiResponse.SuccessBody<List<StudyHistoryResponse>>> browseHistory(@RequestBody StudyHistoryRequest request) {
        List<StudyHistoryResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }
    @ApiOperation(value = "당일 누적 학습 시간 조회 - 마이페이지 Daily 차트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "request", value = "조회 하려는 기간 from, to (daily는 from == to)")
    })
    @GetMapping("/daily")
    public ApiResponse<ApiResponse.SuccessBody<List<StudyHistoryResponse>>> browseDaily(@RequestBody StudyHistoryRequest request) {
        List<StudyHistoryResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }
    @ApiOperation(value = "주간 누적 학습 시간 조회 - 마이페이지 Weekly 차트")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(name = "request", value = "조회 하려는 기간 from, to")
    })
    @GetMapping("/weekly")
    public ApiResponse<ApiResponse.SuccessBody<List<StudyHistoryResponse>>> browseWeekly(@RequestBody StudyHistoryRequest request) {
        List<StudyHistoryResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }

}
