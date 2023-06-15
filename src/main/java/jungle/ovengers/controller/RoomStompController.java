package jungle.ovengers.controller;

import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.model.request.RoomJoinRequest;
import jungle.ovengers.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RoomStompController {
    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    @MessageMapping("/add")
    public void add(SimpMessageHeaderAccessor sha, RoomAddRequest request) {
        log.info("방 생성 요청 memberId: {}, {}", sha.getUser()
                                               .getName(), request.toString());
        template.convertAndSend("/topic/" + request.getGroupId(), roomService.generateRoom(Long.valueOf(Objects.requireNonNull(sha.getUser())
                                                                                                               .getName()), request));
    }

    @MessageMapping("/join")
    public void join(SimpMessageHeaderAccessor sha, RoomJoinRequest request) {
        log.info("방 참가 요청 memberId: {}, {}", sha.getUser()
                                                .getName(), request.toString());
        template.convertAndSend("/topic/" + request.getGroupId(), roomService.joinRoom(Long.valueOf(sha.getUser()
                                                                                                       .getName()), request));
    }
}
