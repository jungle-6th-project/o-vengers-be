package jungle.ovengers.controller;

import jungle.ovengers.exception.AccessTokenInvalidException;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiControllerExceptionHandler {

    private static final String FAIL_CODE = "fail";
    private static final String BAD_REQUEST_MESSAGE = "알 수 없는 오류가 발생했어요.";
    private static final String REFRESH_TOKEN_INVALID_MESSAGE = "로그인이 필요해요.";
    private static final String FORBIDDEN_MESSAGE = "접근 권한이 없어요.";
    private static final String UNAUTHORIZED_MESSAGE = "인증이 필요해요.";
    private static final String NOT_FOUND_MESSAGE = "요청과 일치하는 결과를 찾을 수 없어요.";
    private static final String SERVER_ERROR_MESSAGE = "알 수 없는 오류가 발생했어요.";

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<ApiResponse.FailureBody> handleForbidden(AccessDeniedException ex, HttpServletRequest request) {
        return ApiResponseGenerator.fail(FAIL_CODE, FORBIDDEN_MESSAGE, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ApiResponse<ApiResponse.FailureBody> handleUnauthorized(AuthenticationException ex, HttpServletRequest request) {
        return ApiResponseGenerator.fail(FAIL_CODE, UNAUTHORIZED_MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessTokenInvalidException.class)
    public ApiResponse<ApiResponse.FailureBody> handleInvalidToken(AccessTokenInvalidException ex, HttpServletRequest request) {
        return ApiResponseGenerator.fail(FAIL_CODE, BAD_REQUEST_MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
