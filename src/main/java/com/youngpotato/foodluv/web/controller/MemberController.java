package com.youngpotato.foodluv.web.controller;

import com.youngpotato.foodluv.common.response.ResponseService;
import com.youngpotato.foodluv.common.response.SingleResponse;
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

    private final ResponseService responseService;
    private final MemberService memberService;

    /**
     * 일반 회원가입
     */
    @PostMapping("/sign-up")
    public SingleResponse<MemberDTO> signup(@Valid @RequestBody SignUpDTO signUpDTO) {
        MemberDTO memberDTO = memberService.signup(signUpDTO);
        return responseService.getSingleResponse(memberDTO);
    }

    /**
     * 로그인
     */
    @PostMapping("/sign-in")
    public SingleResponse<JwtDTO> signIn(@Valid @RequestBody SignInDTO signInDTO) {
        JwtDTO jwtDTO = memberService.signIn(signInDTO);
        return responseService.getSingleResponse(jwtDTO);
    }

    /**
     * 토큰 재발행
     */
    @PostMapping("/reissue")
    public SingleResponse<JwtDTO> reissue(@RequestBody TokenDTO tokenDTO) {
        JwtDTO jwtDTO = memberService.reissue(tokenDTO);
        return responseService.getSingleResponse(jwtDTO);
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
