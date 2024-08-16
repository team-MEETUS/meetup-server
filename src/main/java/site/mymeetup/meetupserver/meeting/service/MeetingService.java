package site.mymeetup.meetupserver.meeting.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.util.List;

import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSelectRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingMemberDto.MeetingMemberReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingMemberDto.MeetingMemberRespDto;

public interface MeetingService {
    MeetingSaveRespDto createMeeting(Long crewId, MeetingSaveReqDto meetingSaveReqDto, MultipartFile image, CustomUserDetails userDetails);

    MeetingSaveRespDto updateMeeting(Long crewId, Long meetingId, MeetingSaveReqDto meetingSaveReqDto, CustomUserDetails userDetails);

    void deleteMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails);

    List<MeetingSelectRespDto> getMeetingByCrewId(Long crewId, String status);

    void attendMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails);

    void cancelMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails);

    void rejectMeeting(Long crewId, Long meetingId, MeetingMemberReqDto meetingMemberReqDto, CustomUserDetails userDetails);

    List<MeetingMemberRespDto> getMeetingMemberByMeetingId(Long crewId, Long meetingId);
}
