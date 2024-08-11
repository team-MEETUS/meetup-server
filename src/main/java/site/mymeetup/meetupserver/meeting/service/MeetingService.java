package site.mymeetup.meetupserver.meeting.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSelectRespDto;

public interface MeetingService {
    MeetingSaveRespDto createMeeting(Long crewId, MeetingSaveReqDto meetingSaveReqDto, MultipartFile image);

    MeetingSaveRespDto updateMeeting(Long crewId, Long meetingId, MeetingSaveReqDto meetingSaveReqDto);

    void deleteMeeting(Long crewId, Long meetingId);

    List<MeetingSelectRespDto> getMeetingByCrewId(Long crewId, String status);

    // 정모 참여 멤버
    void attendMeeting(Long crewId, Long meetingId);

    void cancelMeeting(Long crewId, Long meetingId);
}
