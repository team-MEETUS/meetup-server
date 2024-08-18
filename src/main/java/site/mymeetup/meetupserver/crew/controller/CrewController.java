package site.mymeetup.meetupserver.crew.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.crew.service.CrewService;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.util.List;

import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewDetailRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewInterestReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {
    private final CrewService crewService;

    // 모임 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<CrewSaveRespDto> createCrew(@RequestPart @Valid CrewSaveReqDto crewSaveReqDto,
                                                   @RequestPart MultipartFile image,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.createCrew(crewSaveReqDto, image, userDetails));
    }

    // 모임 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}")
    public ApiResponse<CrewSaveRespDto> updateCrew(@PathVariable("crewId") Long crewId,
                                                   @RequestPart @Valid CrewSaveReqDto crewSaveReqDto,
                                                   @RequestPart MultipartFile image,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.updateCrew(crewId, crewSaveReqDto, image, userDetails));
    }

    // 모임 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}")
    public ApiResponse<Void> deleteCrew(@PathVariable("crewId") Long crewId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        crewService.deleteCrew(crewId, userDetails);
        return ApiResponse.success(null);
    }

    // 특정 모임 상세 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}")
    public ApiResponse<CrewDetailRespDto> getCrewByCrewId(@PathVariable("crewId") Long crewId) {
        return ApiResponse.success(crewService.getCrewByCrewId(crewId));
    }

    // 관심사 별 모임 조회
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/interests")
    public ApiResponse<List<CrewSelectRespDto>> getAllCrewByInterest(@RequestBody CrewInterestReqDto crewInterestReqDto,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.getAllCrewByInterest(crewInterestReqDto, userDetails));
    }

    // 내 모임 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public ApiResponse<List<CrewSelectRespDto>> getMyCrew(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.getMyCrew(userDetails));
    }

    // 모임 권한 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/members/me")
    public ApiResponse<CrewMemberRole> getCrewMemberRole(@PathVariable("crewId") Long crewId,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.getCrewMemberRole(crewId, userDetails));
    }

    // 모임 가입 신청
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/members")
    public ApiResponse<CrewMemberSaveRespDto> createCrewMember(@PathVariable("crewId") Long crewId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.signUpCrew(crewId, userDetails));
    }

    // 특정 모임의 모임원 || 가입 신청 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/members")
    public ApiResponse<List<CrewMemberSelectRespDto>> getCrewMemberByCrewId(@PathVariable("crewId") Long crewId,
                                                                            @RequestParam(value = "status", defaultValue = "members") String status,
                                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CrewMemberSelectRespDto> response = null;

        if ("members".equals(status)) {
            response = crewService.getCrewMemberByCrewId(crewId);
        } else if ("signup".equals(status)) {
            response = crewService.getSignUpMemberByCrewId(crewId, userDetails);
        } else {
            throw new CustomException(ErrorCode.INVALID_PATH);
        }

        return ApiResponse.success(response);
    }

    // 모임원 권한 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}/members")
    public ApiResponse<CrewMemberSaveRespDto> updateCrewMember(@PathVariable("crewId") Long crewId,
                                                               @RequestBody CrewMemberSaveReqDto crewMemberSaveReqDto,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.updateRole(crewId, crewMemberSaveReqDto, userDetails));
    }

    // 모임 찜
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/likes")
    public ApiResponse<Void> likeCrew(@PathVariable("crewId") Long crewId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        crewService.likeCrew(crewId, userDetails);
        return ApiResponse.success(null);
    }

    // 모임 찜 여부 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/likes")
    public ApiResponse<Boolean> isLikeCrew(@PathVariable("crewId") Long crewId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(crewService.isLikeCrew(crewId, userDetails));
    }
}
