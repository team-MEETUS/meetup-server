package site.mymeetup.meetupserver.member.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.dto.MemberDto.*;
import site.mymeetup.meetupserver.member.service.MemberService;
import site.mymeetup.meetupserver.response.ApiResponse;


import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    // 회원 가입
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    public ApiResponse<MemberSaveRespDto> createMember(@RequestBody @Valid MemberSaveReqDto memberSaveReqDto) {
        MemberSaveRespDto memberSaveRespDto = memberService.createMember(memberSaveReqDto);
        return ApiResponse.success(memberSaveRespDto);
    }
    // 로그인 사용자 정보 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}/memberInfo")
    public ApiResponse<MemberInfoDto> getMemberInfo(@PathVariable Long memberId,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.getUserInfoByMemberId(memberId, userDetails));
    }

    // SNS 로그인 요청 리디렉션
    @GetMapping("/oauth2")
    public void authorize(@RequestParam String provider, HttpServletResponse response) throws IOException {
        String redirectUrl = "http://localhost:8082/oauth2/authorization/" + provider;
        response.sendRedirect(redirectUrl);
    }

    // OAuth2 콜백 처리
    @GetMapping("/login/oauth2/code/{provider}")
    public ApiResponse<String> callback(@PathVariable String provider, @RequestParam String code, HttpServletResponse response) {
        try {
            // 인증 코드를 사용하여 액세스 토큰을 요청
            String accessToken = memberService.getAccessTokenFromProvider(provider, code);
            CustomUserDetails userDetails = memberService.getUserInfoFromProvider(provider, accessToken);

            // 사용자 정보로 JWT 생성
            String token = jwtUtil.createJwt(userDetails.getAuthorities().toString(), userDetails.getMemberId(), 7 * 24 * 60 * 60 * 1000L);

            // 쿠키에 JWT 추가
            response.addCookie(createCookie("Authorization", token));

            // 프론트엔드 애플리케이션으로 리디렉션
            String redirectUrl = "http://localhost:3000";
            response.sendRedirect(redirectUrl);

            return ApiResponse.success("Login successful");
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MEMBER_AUTHENTICATION_FAILED);
        }
    }

    private Cookie createCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }

    // 회원 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public ApiResponse<MemberUpdateRespDto> updateMember(@PathVariable("memberId") Long memberId,
                                                         @RequestPart @Valid MemberUpdateReqDto memberUpdateReqDto,
                                                         @RequestPart MultipartFile image,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.updateMember(memberId, memberUpdateReqDto, image, userDetails));
    }

    // 회원 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public ApiResponse<MemberSaveRespDto> deleteMember(@PathVariable Long memberId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.deleteMember(memberId, userDetails));
    }

    // 특정 회원 조회(삭제, 비활성 회원 포함)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    public ApiResponse<MemberSelectRespDto> getMemberByMemberId(@PathVariable Long memberId,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.getMemberByMemberId(memberId, userDetails));
    }

    // 문자 인증
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/phoneCheck")
    public ApiResponse<MemberSMSRespDto> sendSMS(@RequestBody MemberSMSReqDto memberSMSReqDto) {
        return ApiResponse.success(memberService.sendSMS(memberSMSReqDto));
    }

}
