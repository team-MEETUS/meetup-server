package site.mymeetup.meetupserver.crew.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.service.CrewService;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.util.List;

import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewLikeDto.CrewLikeSaveRespDto;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    // 모임 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<CrewSaveRespDto> createCrew(@RequestPart MultipartFile image,
                                     @RequestPart @Valid CrewSaveReqDto crewSaveReqDto) {
        return ApiResponse.success(crewService.createCrew(crewSaveReqDto, image));
    }

    // 모임 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}")
    public ApiResponse<CrewSaveRespDto> updateCrew(@PathVariable("crewId") Long crewId,
                                     @RequestPart MultipartFile image,
                                     @RequestPart @Valid CrewSaveReqDto crewSaveReqDto) {
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
    public ApiResponse<CrewSelectRespDto> getCrewByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getCrewByCrewId(crewId));
    }

    // 관심사 별 모임 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiResponse<List<CrewSelectRespDto>> getAllCrew(@RequestParam(required = false) String city,
                                                           @RequestParam(required = false) Long interestBigId,
                                                           @RequestParam(required = false) Long interestSmallId,
                                                           @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success(crewService.getAllCrewByInterest(city, interestBigId, interestSmallId, page));
    }

    // 모임 가입 신청
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/signup-members")
    public ApiResponse<CrewMemberSaveRespDto> createCrewMember(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.signUpCrew(crewId));
    }

    // 특정 모임의 모임원 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/members")
    public ApiResponse<List<CrewMemberSelectRespDto>> getCrewMemberByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getCrewMemberByCrewId(crewId));
    }

    // 특정 모임의 가입 신청 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/signup-members")
    public ApiResponse<List<CrewMemberSelectRespDto>> getSignUpMemberByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getSignUpMemberByCrewId(crewId));
    }

    // 모임원 권한 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}/members")
    public ApiResponse<CrewMemberSaveRespDto> updateCrewMember(@PathVariable("crewId") Long crewId,
                                                         @RequestBody CrewMemberSaveReqDto crewMemberSaveReqDto) {
        return ApiResponse.success(crewService.updateRole(crewId, crewMemberSaveReqDto));
    }

    // 모임 찜
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/likes")
    public ApiResponse<CrewLikeSaveRespDto> likeCrew(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.likeCrew(crewId));
    }

    // 모임 찜 취소
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}/likes")
    public ApiResponse<Void> deleteLikeCrew(@PathVariable("crewId") Long crewId) {
        crewService.deleteLikeCrew(crewId);
        return ApiResponse.success(null);
    }

    // 모임 찜 여부 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/likes")
    public ApiResponse<Boolean> isLikeCrew(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.isLikeCrew(crewId));
    }
}
