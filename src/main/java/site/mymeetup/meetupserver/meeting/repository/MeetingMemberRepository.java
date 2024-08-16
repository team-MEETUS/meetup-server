package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.meeting.entity.Meeting;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;

import java.util.List;
import java.util.Optional;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {
    // 정모 참석 여부 확인
    boolean existsByMeetingAndCrewMember(Meeting meeting, CrewMember crewMember);

    // 정모에 참석한 특정 유저 가져오기
    Optional<MeetingMember> findByMeetingAndCrewMember(Meeting meeting, CrewMember crewMember);

    // 정모에 참석하는 멤버 리스트 가져오기
    List<MeetingMember> findByMeeting(Meeting meeting);
}
