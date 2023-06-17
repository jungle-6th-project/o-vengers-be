package jungle.ovengers.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.request.WholeRoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.service.RoomService;
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
}
