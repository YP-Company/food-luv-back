package com.youngpotato.foodluv.web.controller;

import com.youngpotato.foodluv.common.util.SecurityUtil;
import com.youngpotato.foodluv.domain.member.Member;
import com.youngpotato.foodluv.service.MemberService;
import com.youngpotato.foodluv.web.dto.JwtDTO;
import com.youngpotato.foodluv.web.dto.MemberDTO;
import com.youngpotato.foodluv.web.dto.SignInDTO;
import com.youngpotato.foodluv.web.dto.SignUpDTO;
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
     * test
     */
    @GetMapping("/get-test")
    public ResponseEntity<String> test() {
        String email = SecurityUtil.getCurrentEmail();
        return ResponseEntity.ok("success");
    }
    @GetMapping("/user/get-test")
    public ResponseEntity<String> test1() {
        return ResponseEntity.ok("success");
    }
    @GetMapping("/manager/get-test")
    public ResponseEntity<String> test2() {
        return ResponseEntity.ok("success");
    }
    @GetMapping("/admin/get-test")
    public ResponseEntity<String> test3() {
        return ResponseEntity.ok("success");
    }

    /**
     * 일반 회원가입
     */
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDTO> signup(@Valid @RequestBody SignUpDTO signUpDTO) {
        Member member = memberService.signup(signUpDTO);
        MemberDTO memberDTO = MemberDTO.from(member);
        return ResponseEntity.ok(memberDTO);
    }

    /**
     * 로그인
     */
    @PostMapping("/sign-in")
    public ResponseEntity<JwtDTO> signIn(@RequestBody SignInDTO signInDTO) {
        JwtDTO jwtDTO = memberService.signIn(signInDTO);
        return ResponseEntity.ok(jwtDTO);
    }
}
