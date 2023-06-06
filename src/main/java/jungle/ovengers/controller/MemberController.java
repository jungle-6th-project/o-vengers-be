package jungle.ovengers.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    @GetMapping("/")
    public String home() {
        return "success";
    }

    @GetMapping("/oauth/kakao")
    public void test(@RequestParam String code) {
        System.out.println("테스트 : " + code);
    }
}
