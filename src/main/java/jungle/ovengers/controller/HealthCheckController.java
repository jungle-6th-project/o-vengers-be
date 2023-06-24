package jungle.ovengers.controller;

import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@ApiIgnore
@Slf4j
public class HealthCheckController {

    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<Void>> check() {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.SUCCESS);
    }
}
