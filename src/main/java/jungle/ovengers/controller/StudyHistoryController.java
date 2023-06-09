package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.StudyHistoryRequest;
import jungle.ovengers.model.response.StudyHistoryResponse;
import jungle.ovengers.service.StudyHistoryService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/study-histories")
@Slf4j
public class StudyHistoryController {

    private final StudyHistoryService studyHistoryService;

    @ApiOperation(value = "마이페이지 일자별 학습 누적 시간 조회 - 당일, 주간, 월간")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping("/durations")
    public ApiResponse<ApiResponse.SuccessBody<List<StudyHistoryResponse>>> readDaily(StudyHistoryRequest request) {
        return ApiResponseGenerator.success(studyHistoryService.getDailyDuration(request), HttpStatus.OK, MessageCode.SUCCESS);
    }
}
