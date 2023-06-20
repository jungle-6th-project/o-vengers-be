package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.RoomHistoryRequest;
import jungle.ovengers.model.request.WholeRoomBrowseRequest;
import jungle.ovengers.model.response.RoomHistoryResponse;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.service.RoomService;
import jungle.ovengers.support.ApiResponse;
import jungle.ovengers.support.ApiResponseGenerator;
import jungle.ovengers.support.MessageCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @ApiOperation(value = "그룹 내 전체 방 예약 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping
    public ApiResponse<ApiResponse.SuccessBody<List<RoomResponse>>> browse(RoomBrowseRequest request) {
        return ApiResponseGenerator.success(roomService.getRooms(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "그룹 내 사용자가 예약해 놓은 방 예약 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping("/reservation")
    public ApiResponse<ApiResponse.SuccessBody<List<RoomResponse>>> browseWhichReserved(RoomBrowseRequest request) {
        return ApiResponseGenerator.success(roomService.getJoinedRooms(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "전체 그룹 중 내 전체 방 예약 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping("/all")
    public ApiResponse<ApiResponse.SuccessBody<List<RoomResponse>>> browseByAllGroups(WholeRoomBrowseRequest request) {
        return ApiResponseGenerator.success(roomService.getRoomsByAllGroups(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @ApiOperation(value = "방 입장시 해당 방 입장 시간 기록 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PostMapping("/histories")
    public ApiResponse<ApiResponse.SuccessBody<RoomHistoryResponse>> addEntryHistory(@RequestBody RoomHistoryRequest request) {
        return ApiResponseGenerator.success(roomService.generateRoomEntryHistory(request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "방 나가기 요청시 해당 방 퇴장 기록 생성")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @PatchMapping("/histories")
    public ApiResponse<ApiResponse.SuccessBody<RoomHistoryResponse>> editExitHistory(@RequestBody RoomHistoryRequest request) {
        return ApiResponseGenerator.success(roomService.updateRoomExitHistory(request), HttpStatus.CREATED, MessageCode.RESOURCE_CREATED);
    }

    @ApiOperation(value = "사용자가 입장 가능한 가장 빠른 방 정보 조회")
    @ApiImplicitParam(name = "Authorization", value = "JWT token", required = true, dataTypeClass = String.class, paramType = "header")
    @GetMapping("/nearest")
    public ApiResponse<ApiResponse.SuccessBody<RoomResponse>> readNearest() {
        return ApiResponseGenerator.success(roomService.getNearestRoom(), HttpStatus.OK, MessageCode.SUCCESS);
    }
}
