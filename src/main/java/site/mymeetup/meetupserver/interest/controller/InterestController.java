package site.mymeetup.meetupserver.interest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import site.mymeetup.meetupserver.interest.service.InterestService;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    // 전체 관심사 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/interestBigs")
    public ApiResponse<?> getAllInterestBig() {
        return ApiResponse.success(interestService.getAllInterestBig());
    }

    // 특정 관심사의 상세 관심사 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/interestBigs/{interestBigId}/interestSmalls")
    public ApiResponse<?> getAllInterestSmallByInterestBigId(@PathVariable Long interestBigId) {
        return ApiResponse.success(interestService.getAllInterestSmallByInterestBigId(interestBigId));
    }
}
