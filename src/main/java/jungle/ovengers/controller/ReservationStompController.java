package jungle.ovengers.controller;

import jungle.ovengers.model.request.ReservationRequest;
import jungle.ovengers.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationStompController {

    private final SimpMessagingTemplate template;
    private final ReservationService reservationService;

    @MessageMapping("/create")
    public void test(ReservationRequest request) {
        System.out.println(request.getFrom());
        System.out.println(request.getTo());
        System.out.println(request.getGroupId());
        System.out.println("호출됨");
        template.convertAndSend("/topic/test", "수신 : " + request.getFrom());
    }
}
