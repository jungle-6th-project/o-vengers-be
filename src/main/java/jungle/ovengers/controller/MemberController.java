package jungle.ovengers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    @GetMapping("/oauth/kakao")
    public void test(@RequestParam String code) {
        System.out.println("테스트 : " + code);
    }
}
