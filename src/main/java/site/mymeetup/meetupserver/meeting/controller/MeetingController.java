package site.mymeetup.meetupserver.meeting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.meeting.service.MeetingService;
import site.mymeetup.meetupserver.response.ApiResponse;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingService meetingService;

    // 정모 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/meetings")
    public ApiResponse<MeetingSaveRespDto> createMeeting(@PathVariable("crewId") Long crewId,
                                                         @RequestPart MultipartFile image,
                                                         @RequestPart @Valid MeetingSaveReqDto meetingSaveReqDto) {
        return ApiResponse.success(meetingService.createMeeting(crewId, meetingSaveReqDto, image));
    }

    // 정모 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{crewId}/meetings/{meetingId}")
    public ApiResponse<MeetingSaveRespDto> updateMeeting(@PathVariable("crewId") Long crewId,
                                                         @PathVariable("meetingId") Long meetingId,
                                                         @RequestPart @Valid MeetingSaveReqDto meetingSaveReqDto) {
        return ApiResponse.success(meetingService.updateMeeting(crewId, meetingId, meetingSaveReqDto));
    }

    // 모임 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}/meetings/{meetingId}")
    public ApiResponse<?> deleteCrew(@PathVariable("crewId") Long crewId,
                                     @PathVariable("meetingId") Long meetingId) {
        meetingService.deleteMeeting(crewId, meetingId);
        return ApiResponse.success(null);
    }
}
