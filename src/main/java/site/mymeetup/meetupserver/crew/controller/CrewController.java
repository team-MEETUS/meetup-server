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
        CrewDto.CrewSaveRespDto crewSaveRespDto = crewService.createCrew(crewSaveReqDto, image);
        return ApiResponse.success(crewSaveRespDto);
    }

    // 모임 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}")
    public ApiResponse<?> updateCrew(@PathVariable("crewId") Long crewId,
                                     @RequestPart MultipartFile image,
                                     @RequestPart @Valid CrewDto.CrewSaveReqDto crewSaveReqDto) {
        CrewDto.CrewSaveRespDto crewSaveRespDto = crewService.updateCrew(crewId, crewSaveReqDto, image);
        return ApiResponse.success(crewSaveRespDto);
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
}
