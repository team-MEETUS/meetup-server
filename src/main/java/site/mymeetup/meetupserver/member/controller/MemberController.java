package site.mymeetup.meetupserver.member.controller;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.MemberDto;
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
    public ApiResponse<?> createMember(@RequestBody @Valid MemberDto.MemberSaveReqDto memberSaveReqDto) {
        MemberDto.MemberSaveRespDto memberSaveRespDto = memberService.createMember(memberSaveReqDto);
        return ApiResponse.success(memberSaveRespDto);
    }

    // 특정 회원 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{memberId}")
    public ApiResponse<?> getMemberByMemberId(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(memberService.getMemberByMemberId(memberId));
    }

    // 회원 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{memberId}")
    public ApiResponse<?> updateMember(@PathVariable("memberId") Long memberId,
                                       @RequestPart MultipartFile image,
                                       @RequestPart @Valid MemberDto.MemberSaveReqDto memberSaveReqDto) {
        MemberDto.MemberSaveRespDto memberSaveRespDto = memberService.updateMember(memberId, memberSaveReqDto, image);
        return ApiResponse.success(memberSaveRespDto);
    }
}
