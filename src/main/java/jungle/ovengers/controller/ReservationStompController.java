package jungle.ovengers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationStompController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/create")
    public void test(String message) {
        System.out.println(message);
        System.out.println("호출됨");
        template.convertAndSend("/topic/test", "수신 : " + message);
    }
}
