package jungle.ovengers.controller;

import jungle.ovengers.model.response.Token;
import jungle.ovengers.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/kakao")
    public String kakaoCallback(@RequestParam String code) {
        System.out.println(code);
        return code;
    }

    @PostMapping("/tokens")
    public ResponseEntity<Token> publishToken(@RequestParam String authCode) {
        return ResponseEntity.ok(memberService.publishToken(authCode));
    }
}
