package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.RankBrowseRequest;
import jungle.ovengers.model.response.RankResponse;
import jungle.ovengers.service.RankService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranks")
@Slf4j
public class RankController {

    private final RankService rankService;

    @ApiOperation(value = "그룹원 조회/그룹내 랭킹 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<RankResponse>>> browse(RankBrowseRequest request) {
        List<RankResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }
}
