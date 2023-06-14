package jungle.ovengers.controller;


import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.TodoAddRequest;
import jungle.ovengers.model.request.TodoDeleteRequest;
import jungle.ovengers.model.request.TodoEditRequest;
import jungle.ovengers.model.request.TodoReadRequest;
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
    public ApiResponse<ApiResponse.SuccessBody<List<TodoResponse>>> browse(TodoReadRequest request) {
        return ApiResponseGenerator.success(todoService.getGroupTodos(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 Todo 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PostMapping
    public ApiResponse<ApiResponse.SuccessBody<TodoResponse>> add(@RequestBody TodoAddRequest request) {
        return ApiResponseGenerator.success(todoService.generateTodo(request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 Todo 수정 (내용 수정 or 완료 체크)")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PatchMapping
    public ApiResponse<ApiResponse.SuccessBody<TodoResponse>> edit(@RequestBody TodoEditRequest request) {
        return ApiResponseGenerator.success(todoService.changeTodoInfo(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 Todo 삭제")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @DeleteMapping
    public ApiResponse<ApiResponse.SuccessBody<Void>> delete(@RequestBody TodoDeleteRequest request) {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED);
    }
}
