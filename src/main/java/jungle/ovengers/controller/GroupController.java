package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.request.GroupDeleteRequest;
import jungle.ovengers.model.request.GroupEditRequest;
import jungle.ovengers.model.response.GroupResponse;
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
@RequestMapping("/api/v1/groups")
@Slf4j
public class GroupController {

    @ApiOperation(value = "그룹 전체 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<GroupResponse>>> browse() {
        List<GroupResponse> responses = new ArrayList<>();
        return ApiResponseGenerator.success(responses, HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PostMapping
    public ApiResponse<ApiResponse.SuccessBody<GroupResponse>> add(@RequestBody GroupAddRequest request) {
        return ApiResponseGenerator.success(new GroupResponse(1L, "groupName", false), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 수정")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PutMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<GroupResponse>> edit(@RequestBody GroupEditRequest request) {
        return ApiResponseGenerator.success(new GroupResponse(1L, "groupName", false), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 삭제")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @DeleteMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<Void>> delete(@RequestBody GroupDeleteRequest request) {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED);
    }
}
