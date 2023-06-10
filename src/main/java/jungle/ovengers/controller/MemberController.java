package jungle.ovengers.controller;

import io.swagger.v3.oas.annotations.Operation;
import jungle.ovengers.model.request.AuthRequest;
import jungle.ovengers.model.response.Token;
import jungle.ovengers.service.MemberService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
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


    @PostMapping("/tokens")
    public ApiResponse<ApiResponse.SuccessBody<Token>> publishToken(@RequestBody AuthRequest authRequest) {
        return ApiResponseGenerator.success(memberService.publishToken(authRequest), HttpStatus.OK);
    }
}
