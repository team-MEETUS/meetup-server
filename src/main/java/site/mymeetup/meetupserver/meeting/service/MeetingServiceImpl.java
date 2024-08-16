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
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSaveRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingDto.MeetingSelectRespDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingMemberDto.MeetingMemberReqDto;
import static site.mymeetup.meetupserver.meeting.dto.MeetingMemberDto.MeetingMemberRespDto;

@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {
    private final MemberRepository memberRepository;
    private final MeetingRepository meetingRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MeetingMemberRepository meetingMemberRepository;
    private final S3ImageService s3ImageService;

    // 정모 생성
    public MeetingSaveRespDto createMeeting(Long crewId, MeetingSaveReqDto meetingSaveReqDto, MultipartFile image, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

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
    public MeetingSaveRespDto updateMeeting(Long crewId, Long meetingId, MeetingSaveReqDto meetingSaveReqDto, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 정모 업데이트
        meeting.updateMeeting(meetingSaveReqDto.toEntity(meeting.getOriginalImg(), meeting.getSaveImg(), meeting.getCrew(), meeting.getCrewMember()));

        // DB 수정
        Meeting updateMeeting = meetingRepository.save(meeting);

        return MeetingSaveRespDto.builder().meeting(updateMeeting).build();
    }

    @Override
    public void deleteMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 정모 업데이트
        meeting.deleteMeeting(0);

        // DB 수정
        meetingRepository.save(meeting);
    }

    // 모임별 정모 조회
    @Override
    public List<MeetingSelectRespDto> getMeetingByCrewId(Long crewId, String status) {
        // crew 검증
        Crew crew = validateCrew(crewId);

        // 오늘 정시 날짜
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();

        // status 구분에 따라 정모 리스트 가져오기
        List<Meeting> meetings;
        if (status.equals("upcoming")) {
            meetings = meetingRepository.findMeetingsWithMembers(crewId, 1, startOfToday, true);
        } else if (status.equals("past")) {
            meetings = meetingRepository.findMeetingsWithMembers(crewId, 1, startOfToday, false);
        } else {
            throw new CustomException(ErrorCode.MEETING_INVALID_STATUS);
        }

        return meetings.stream()
                .map(MeetingSelectRespDto::new)
                .toList();
    }

    // MeetingMember

    // 정모 참석
    @Override
    public void attendMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 해당 정모에 참여하지 않은 멤버인지 확인
        if (meetingMemberRepository.existsByMeetingAndCrewMember(meeting, crewMember)) {
            throw new CustomException(ErrorCode.ALREADY_ATTEND_MEETING);
        }

        // 정원을 초과하지 않았는지 확인
        if (meeting.getAttend() == meeting.getMax()) {
            throw new CustomException(ErrorCode.MEETING_FULL);
        }

        // 정모 멤버 등록
        MeetingMember meetingMember = MeetingMember.builder()
        .meeting(meeting)
        .crewMember(crewMember)
        .build();
        meetingMemberRepository.save(meetingMember);

        // 정모 참석인원 +1
        meeting.changeAttend(1);
        meetingRepository.save(meeting);
    }

    // 정모 참석 취소
    @Override
    public void cancelMeeting(Long crewId, Long meetingId, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // 정모생성이 가능한 유저인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 해당 정모에 참여한 멤버인지 확인
        MeetingMember meetingMember = meetingMemberRepository.findByMeetingAndCrewMember(meeting, crewMember)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_ATTEND_MEETING));

        // 정모 멤버 삭제
        meetingMemberRepository.delete(meetingMember);

        // 정모 참석인원 -1
        meeting.changeAttend(-1);
        meetingRepository.save(meeting);
    }

    // 정모 참석 거부
    @Override
    public void rejectMeeting(Long crewId, Long meetingId, MeetingMemberReqDto meetingMemberReqDto, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // crew 검증
        Crew crew = validateCrew(crewId);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 로그인 한 유저가 운영진 또는 모임장인지 검증
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        CrewMember crewMember = validateCrewMember(crew, member, roles);

        // 상대 유저가 일반 멤버인지 검증
        Member targetMember = validateMember(meetingMemberReqDto.getMemberId());

        roles = Arrays.asList(
                CrewMemberRole.MEMBER
        );
        CrewMember targetCrewMember = validateCrewMember(crew, targetMember, roles);

        // 정모 참여 멤버 가져오기
        MeetingMember meetingMember = meetingMemberRepository.findByMeetingAndCrewMember(meeting, targetCrewMember)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_MEMBER_NOT_FOUND));

        // 정모 멤버 삭제
        meetingMemberRepository.delete(meetingMember);

        // 정모 참석인원 -1
        meeting.changeAttend(-1);
        meetingRepository.save(meeting);
    }

    // 특정 정모의 참여 멤버 조회
    @Override
    public List<MeetingMemberRespDto> getMeetingMemberByMeetingId(Long crewId, Long meetingId) {
        // crew 검증
        Crew crew = validateCrew(crewId);

        // meeting 검증
        Meeting meeting = validateMeeting(meetingId, crew);

        // 참여 멤버 조회
        List<MeetingMember> meetingMembers = meetingMemberRepository.findByMeeting(meeting);

        return meetingMembers.stream()
                .map(MeetingMemberRespDto::new)
                .toList();
    }

    // 사용자 검증 후 MEMBER 엔티티 반환
    private Member validateMember(Long memberId) {
        return memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 모임 검증 후 CREW 엔티티 반환
    private Crew validateCrew(Long crewId) {
        return crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
    }

    // 모임 멤버 검증 후 CREWMEMBER 엔티티 반환
    private CrewMember validateCrewMember(Crew crew, Member member, List<CrewMemberRole> roles) {
        return crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_ACCESS_DENIED));
    }

    // 정모 검증 후 MEETING 엔티티 반환
    private Meeting validateMeeting(Long meetingId, Crew crew) {
        return meetingRepository.findByMeetingIdAndCrewAndStatus(meetingId, crew, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEETING_NOT_FOUND));
    }

}
