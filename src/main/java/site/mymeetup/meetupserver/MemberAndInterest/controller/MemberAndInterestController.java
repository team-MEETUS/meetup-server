package site.mymeetup.meetupserver.MemberAndInterest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.mymeetup.meetupserver.MemberAndInterest.service.MemberAndInterestService;
import site.mymeetup.meetupserver.response.ApiResponse;

import static site.mymeetup.meetupserver.response.ApiResponse.success;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberAndInterestController {
    private final MemberAndInterestService memberAndInterestService;

    //전체 멤버관심사 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/memberinterests")
    public ApiResponse<?> getAllMemberAndInterest() {
        return success(memberAndInterestService.getAllMemberAndInterest());
    }
}
