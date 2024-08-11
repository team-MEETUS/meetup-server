package site.mymeetup.meetupserver.meeting.service;

import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;

public interface MeetingService {
    MeetingSaveRespDto createMeeting(Long crewId, MeetingSaveReqDto meetingSaveReqDto, MultipartFile image);

    MeetingSaveRespDto updateMeeting(Long crewId, Long meetingId, MeetingSaveReqDto meetingSaveReqDto);
}
