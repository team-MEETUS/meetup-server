package site.mymeetup.meetupserver.crew.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.dto.CrewDto;
import site.mymeetup.meetupserver.crew.service.CrewService;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    // 모임 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<?> createCrew(@RequestPart MultipartFile image,
                                     @RequestPart @Valid CrewDto.CrewSaveReqDto crewSaveReqDto) {
        return ApiResponse.success(crewService.createCrew(crewSaveReqDto, image));
    }

    // 모임 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}")
    public ApiResponse<?> updateCrew(@PathVariable("crewId") Long crewId,
                                     @RequestPart MultipartFile image,
                                     @RequestPart @Valid CrewDto.CrewSaveReqDto crewSaveReqDto) {
        return ApiResponse.success(crewService.updateCrew(crewId, crewSaveReqDto, image));
    }

    // 모임 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}")
    public ApiResponse<?> deleteCrew(@PathVariable("crewId") Long crewId) {
        crewService.deleteCrew(crewId);
        return ApiResponse.success(null);
    }

    // 특정 모임 상세 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}")
    public ApiResponse<?> getCrewByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getCrewByCrewId(crewId));
    }

    // 모임 가입 신청
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}")
    public ApiResponse<?> createCrewMember(@PathVariable("crewId") Long crewId) {
        crewService.signUpCrew(crewId);
        return ApiResponse.success(null);
    }

    // 관심사 별 모임 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiResponse<?> getAllCrew(@RequestParam(required = false) String city,
                                     @RequestParam(required = false) Long interestBigId,
                                     @RequestParam(required = false) Long interestSmallId,
                                     @RequestParam(defaultValue = "0") int page) {

        return ApiResponse.success(crewService.getAllCrewByInterest(city, interestBigId, interestSmallId, page));
    }

    // 특정 모임의 모임원 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/members")
    public ApiResponse<?> getCrewMemberByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getCrewMemberByCrewId(crewId));
    }
    // 특정 모임의 가입 신청 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/signup-members")
    public ApiResponse<?> getSignUpMemberByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getSignUpMemberByCrewId(crewId));
    }
}
