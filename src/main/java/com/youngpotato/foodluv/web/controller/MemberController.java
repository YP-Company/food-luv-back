package com.youngpotato.foodluv.web.controller;

import com.youngpotato.foodluv.service.MemberService;
import com.youngpotato.foodluv.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDTO> signup(@Valid @RequestBody SignUpDTO signUpDTO) {
        return ResponseEntity.ok(memberService.signup(signUpDTO));
    }

    /**
     * 로그인
     */
    @PostMapping("/sign-in")
    public ResponseEntity<JwtDTO> signIn(@Valid @RequestBody SignInDTO signInDTO) {
        return ResponseEntity.ok(memberService.signIn(signInDTO));
    }

    /**
     * 토큰 재발행
     */
    @PostMapping("/reissue")
    public ResponseEntity<JwtDTO> reissue(@RequestBody TokenDTO tokenDTO) {
        return ResponseEntity.ok(memberService.reissue(tokenDTO));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/sign-out")
    public ResponseEntity<?> signOut(@RequestBody TokenDTO tokenDTO) {
        memberService.signOut(tokenDTO);
        return ResponseEntity.ok("Successfully signed out");
    }
}
