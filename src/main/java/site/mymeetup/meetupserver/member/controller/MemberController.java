package site.mymeetup.meetupserver.member.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.service.MemberService;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.util.List;

import static site.mymeetup.meetupserver.member.dto.MemberDto.*;
import static site.mymeetup.meetupserver.member.dto.MemberInterestDto.MemberInterestSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberInterestDto.MemberInterestSaveRespDto;


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
        return ApiResponse.success(memberService.createMember(memberSaveReqDto));
    }

    // 로그인 사용자 정보 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/info")
    public ApiResponse<MemberInfoDto> getMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(memberService.getMemberInfo(userDetails));
    }

    // 회원 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public ApiResponse<MemberUpdateRespDto> updateMember(@PathVariable("memberId") Long memberId,
                                                         @RequestPart @Valid MemberUpdateReqDto memberUpdateReqDto,
                                                         @RequestPart(value = "image", required = false) MultipartFile image,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberUpdateRespDto response = memberService.updateMember(memberId, memberUpdateReqDto, image, userDetails);
        return ApiResponse.success(response);
    }

    // 회원 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public ApiResponse<MemberSaveRespDto> deleteMember(@PathVariable("memberId") Long memberId,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(memberId, userDetails);
        return ApiResponse.success(null);
    }

    // 특정 회원 조회(삭제, 비활성 회원 포함)
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    public ApiResponse<MemberSelectRespDto> getMemberByMemberId(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(memberService.getMemberByMemberId(memberId));
    }

    // 문자 인증
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/phoneCheck")
    public ApiResponse<MemberSMSRespDto> sendSMS(@RequestBody MemberSMSReqDto memberSMSReqDto) {
        return ApiResponse.success(memberService.sendSMS(memberSMSReqDto));
    }

    // 회원 관심사 등록
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{memberId}/interests")
    public ApiResponse<List<MemberInterestSaveRespDto>> createMemberInterest(
            @RequestBody @Valid MemberInterestSaveReqDto memberInterestSaveReqDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = memberInterestSaveReqDto.getMemberId();
        List<Long> interestSmallIds = memberInterestSaveReqDto.getInterestSmallId();

        List<MemberInterestSaveRespDto> response = memberService.updateMemberInterests(memberId, interestSmallIds, userDetails);

        return ApiResponse.success(response);
    }

    // 회원 관심사 수정, 삭제
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}/interests")
    public ApiResponse<List<MemberInterestSaveRespDto>> updateMemberInterest(
            @RequestBody @Valid MemberInterestSaveReqDto memberInterestSaveReqDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = memberInterestSaveReqDto.getMemberId();
        List<Long> interestSmallIds = memberInterestSaveReqDto.getInterestSmallId();

        List<MemberInterestSaveRespDto> response = memberService.updateMemberInterests(memberId, interestSmallIds, userDetails);

        return ApiResponse.success(response);
    }
}

