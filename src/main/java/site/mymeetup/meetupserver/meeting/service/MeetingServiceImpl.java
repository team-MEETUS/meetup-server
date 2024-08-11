package site.mymeetup.meetupserver.meeting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.meeting.dto.MeetingDto;
import site.mymeetup.meetupserver.meeting.entity.Meeting;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;
import site.mymeetup.meetupserver.meeting.repository.MeetingMemberRepository;
import site.mymeetup.meetupserver.meeting.repository.MeetingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSelectRespDto;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final S3ImageService s3ImageService;

    // 정모 생성
    public MeetingSaveRespDto createMeeting(Long crewId, MeetingSaveReqDto meetingSaveReqDto, MultipartFile image) {
        // crew 검증
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 현재 로그인한 유저정보 가져오기
        Long memberId = 101L;   // 테스트용

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = crewMemberRepository.findByCrew_CrewIdAndMember_MemberIdAndRoleIn(crew.getCrewId(), memberId, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_ACCESS_DENIED));

        // 진행중인 정모의 개수가 4개 이상인지 확인
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        int meetingCount = meetingRepository.countByCrew_CrewIdAndStatusAndDateAfter(crewId, 1, startOfToday);
        if (meetingCount >= 4) {
            throw new CustomException(ErrorCode.MAX_MEETINGS_EXCEEDED);
        }

        // 이미지 등록
        if (image.isEmpty()) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }

        String saveImg = s3ImageService.upload(image);
        String originalImg = image.getOriginalFilename();

        // 정모 등록
        Meeting meeting = meetingRepository.save(meetingSaveReqDto.toEntity(originalImg, saveImg, crew, crewMember));

        // 정모 멤버 등록
        MeetingMember meetingMember = MeetingMember.builder()
                .meeting(meeting)
                .crewMember(crewMember)
                .build();
        meetingMemberRepository.save(meetingMember);

        return MeetingSaveRespDto.builder().meeting(meeting).build();
    }

    // 정모 수정
    @Override
    public MeetingSaveRespDto updateMeeting(Long crewId, Long meetingId, MeetingSaveReqDto meetingSaveReqDto) {
        // crew 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // meeting 검증
        Meeting meeting = meetingRepository.findByCrew_CrewIdAndMeetingIdAndStatus(crewId, meetingId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));

        // 현재 로그인한 유저정보 가져오기
        Long memberId = 101L;   // 테스트용

        // 정모수정이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = crewMemberRepository.findByCrew_CrewIdAndMember_MemberIdAndRoleIn(crew.getCrewId(), memberId, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_ACCESS_DENIED));

        // 정모 업데이트
        meeting.updateMeeting(meetingSaveReqDto.toEntity(meeting.getOriginalImg(), meeting.getSaveImg(), meeting.getCrew(), meeting.getCrewMember()));

        // DB 수정
        Meeting updateMeeting = meetingRepository.save(meeting);

        return MeetingSaveRespDto.builder().meeting(updateMeeting).build();
    }

    @Override
    public void deleteMeeting(Long crewId, Long meetingId) {
        // crew 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // meeting 검증
        Meeting meeting = meetingRepository.findByCrew_CrewIdAndMeetingIdAndStatus(crewId, meetingId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));

        // 현재 로그인한 유저정보 가져오기
        Long memberId = 101L;   // 테스트용

        // 정모수정이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = crewMemberRepository.findByCrew_CrewIdAndMember_MemberIdAndRoleIn(crew.getCrewId(), memberId, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_ACCESS_DENIED));

        // 정모 업데이트
        meeting.deleteMeeting(0);

        // DB 수정
        meetingRepository.save(meeting);
    }

    @Override
    public List<MeetingSelectRespDto> getMeetingByCrewId(Long crewId, String status) {
        // crew 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 오늘 정시 날짜
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();

        // status 구분에 따라 정모 리스트 가져오기
        List<Meeting> meetings;
        if (status.equals("upcoming")) {
            meetings = meetingRepository.findByCrew_CrewIdAndStatusAndDateAfter(crewId, 1, startOfToday);
        } else if (status.equals("past")) {
            meetings = meetingRepository.findByCrew_CrewIdAndStatusAndDateBefore(crewId, 1, startOfToday);
        } else {
            throw new CustomException(ErrorCode.MEETING_INVALID_STATUS);
        }

        return meetings.stream()
                .map(MeetingSelectRespDto::new)
                .toList();
    }

}
