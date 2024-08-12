package site.mymeetup.meetupserver.member.controller;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.UserInfoDto;

import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.service.MemberService;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")

public class MemberController {
    private final MemberService memberService;

    // 로그인 사용자 정보 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/userInfo")
    public ApiResponse<UserInfoDto> getUserInfo() {
        // 로그인한 사용자의 memberId 가져오기
        Long loginMemberId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMemberId();

        // 서비스에서 사용자 정보 조회 및 DTO 반환
        UserInfoDto userInfoDto = memberService.getUserInfoByMemberId(loginMemberId);

        return ApiResponse.success(userInfoDto);
    }

    // 회원 가입
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/join")
    public ApiResponse<MemberSaveRespDto> createMember(@RequestBody @Valid MemberSaveReqDto memberSaveReqDto) {
        MemberSaveRespDto memberSaveRespDto = memberService.createMember(memberSaveReqDto);
        return ApiResponse.success(memberSaveRespDto);
    }

    // 특정 회원 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    public ApiResponse<MemberSelectRespDto> getMemberByMemberId(@PathVariable Long memberId) {
        return ApiResponse.success(memberService.getMemberByMemberId(memberId));
    }

    // 회원 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public ApiResponse<MemberSaveRespDto> updateMember(@PathVariable("memberId") Long memberId,
                                       @RequestPart MultipartFile image,
                                       @RequestPart @Valid MemberSaveReqDto memberSaveReqDto) {
        MemberSaveRespDto memberRespDto = memberService.updateMember(memberId, memberSaveReqDto, image);
        return ApiResponse.success(memberRespDto);
    }

    //  회원 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public ApiResponse<MemberSaveRespDto> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.success(null);
    }


}