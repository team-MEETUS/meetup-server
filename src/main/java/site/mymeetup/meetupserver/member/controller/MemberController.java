package site.mymeetup.meetupserver.member.controller;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.request.MemberDto;
import site.mymeetup.meetupserver.member.entity.Member;
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
    public ApiResponse<?> createMember(@RequestPart MultipartFile image,
                                       @RequestPart @Valid MemberDto.MemberSaveReqDto memberSaveReqDto) {
          MemberDto.MemberSaveRespDto memberSaveRespDto = memberService.createMember(memberSaveReqDto, image);
          return ApiResponse.success(memberSaveRespDto);
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

    // 회원 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{memberId}")
    public ApiResponse<?> deleteMember(@PathVariable("memberId") Long memberId) {
          memberService.deleteMember(memberId);
          return ApiResponse.success(null);
    }

    // 특정 회원 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/memberId")
    public ApiResponse<?> getMemberByMemberId(@PathVariable("memberId") Long memberId) {
          return ApiResponse.success(memberService.getMemberByMemberId(memberId));
    }

//
//    @PostMapping("api/v1/members/login")
//    public ResponseEntity<MemberDto> login(@RequestBody MemberDto MDto) {
//        MemberDto loginDTO = memberService.login(MemberDto);
//        return ResponseEntity.status(HttpStatus.OK).header(loginDTO.getToken()).body(loginDTO);
//    }

//    @GetMapping("api/v1/members/checkId")
//    public ResponseEntity<?> checkIdDuplicate(@RequestParam String phone) {
//        memberService.checkIdDuplicate(phone);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

//    @PostMapping("api/v1/members/checkPwd")
//    public ResponseEntity<MemberResponseDto> check(
//            @AuthenticationPrincipal Member memberEntity,
//            @RequestBody Map<String, String> request) {
//        String password = request.get("password");
//        MemberResponseDto memberInfo = memberService.check(memberEntity, password);
//        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
//    }


}
