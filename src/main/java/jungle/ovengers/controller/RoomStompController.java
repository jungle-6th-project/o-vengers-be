package jungle.ovengers.controller;

import jungle.ovengers.model.request.RoomAddRequest;
import jungle.ovengers.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomStompController {
    private final SimpMessagingTemplate template;
    private final RoomService roomService;

    @MessageMapping("/room")
    public void test(RoomAddRequest request) {
        template.convertAndSend("/topic/" + request.getGroupId(), roomService.generateRoom(request));
    }
}
