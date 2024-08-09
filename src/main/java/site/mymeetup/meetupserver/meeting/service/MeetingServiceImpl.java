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

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MeetingRepository meetingRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final S3ImageService s3ImageService;

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
        int meetingCount = meetingRepository.countByCrew_CrewIdAndDateAfter(crewId, startOfToday);
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
}
