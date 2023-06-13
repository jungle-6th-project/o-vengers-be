package jungle.ovengers.controller;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.TodoRequest;
import jungle.ovengers.model.response.TodoResponse;
import jungle.ovengers.service.TodoService;
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
@RequestMapping("/api/v1/todos")
@Slf4j
public class TodoController {

    private final TodoService todoService;

    @ApiOperation(value = "그룹 Todo 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<TodoResponse>>> browse() {
        List<TodoResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 Todo 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PostMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<TodoResponse>> add(@PathVariable Long groupId, @RequestBody TodoRequest request) {
        return ApiResponseGenerator.success(todoService.generateTodo(groupId, request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 Todo 수정 (내용 수정 or 완료 체크)")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PutMapping("/{groupId}/{todoId}")
    public ApiResponse<ApiResponse.SuccessBody<TodoResponse>> edit(@PathVariable Long groupId, @PathVariable Long todoId, @RequestBody TodoRequest request) {
        return ApiResponseGenerator.success(new TodoResponse(1L, 2L, "content"), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 Todo 삭제")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @DeleteMapping("/{groupId}/{todoId}")
    public ApiResponse<ApiResponse.SuccessBody<Void>> delete(@PathVariable Long groupId, @PathVariable Long todoId) {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED);
    }
}
