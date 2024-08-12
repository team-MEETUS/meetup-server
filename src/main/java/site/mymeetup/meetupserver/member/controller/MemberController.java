package site.mymeetup.meetupserver.member.controller;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;

import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.service.MemberService;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")

public class MemberController {
    private final MemberService memberService;

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

    // 회원 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public ApiResponse<MemberSaveRespDto> updateMember(@PathVariable("memberId") Long memberId,
                                       @RequestPart MultipartFile image,
                                       @RequestPart @Valid MemberSaveReqDto memberSaveReqDto,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.updateMember(memberId, memberSaveReqDto, image, userDetails));
    }

    //  회원 삭제
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


}