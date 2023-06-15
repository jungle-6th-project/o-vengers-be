package jungle.ovengers.controller;

import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class RoomStompController {
    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    @MessageMapping("/room")
    public void test(SimpMessageHeaderAccessor sha, RoomAddRequest request) {
        template.convertAndSend("/topic/" + request.getGroupId(), roomService.generateRoom(Long.valueOf(Objects.requireNonNull(sha.getUser())
                                                                                                               .getName()), request));
    }
}
