package site.mymeetup.meetupserver.meeting.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.meeting.dto.MeetingDto;

public interface MeetingService {
    MeetingDto.MeetingSaveRespDto createMeeting(Long crewId, MeetingDto.MeetingSaveReqDto meetingSaveReqDto, MultipartFile image);
}
