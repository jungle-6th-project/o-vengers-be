package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.request.GroupDeleteRequest;
import jungle.ovengers.model.request.GroupEditRequest;
import jungle.ovengers.model.response.GroupResponse;
import jungle.ovengers.service.GroupService;
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

    private final GroupService groupService;

    /*
        Todo : 추후에 검색하도록 바뀌어야함
     */
    @ApiOperation(value = "전체 그룹 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping("/all")
    public ApiResponse<ApiResponse.SuccessBody<List<GroupResponse>>> browseAll() {
        return ApiResponseGenerator.success(groupService.getAllGroups(), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "사용자 그룹 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<GroupResponse>>> browse() {
        return ApiResponseGenerator.success(groupService.getMemberGroups(), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PostMapping
    public ApiResponse<ApiResponse.SuccessBody<GroupResponse>> add(@RequestBody GroupAddRequest request) {
        return ApiResponseGenerator.success(groupService.generateGroup(request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 수정")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PutMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<GroupResponse>> edit(@PathVariable Long groupId, @RequestBody GroupEditRequest request) {
        return ApiResponseGenerator.success(new GroupResponse(1L, "groupName", false), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "그룹 삭제")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @DeleteMapping("/{groupId}")
    public ApiResponse<ApiResponse.SuccessBody<Void>> delete(@PathVariable Long groupId, @RequestBody GroupDeleteRequest request) {
        return ApiResponseGenerator.success(HttpStatus.OK, MessageCode.RESOURCE_DELETED);
    }
}
