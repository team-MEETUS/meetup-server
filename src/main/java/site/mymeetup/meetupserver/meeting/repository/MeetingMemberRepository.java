package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.meeting.entity.Meeting;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;

import java.util.List;
import java.util.Optional;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

    boolean existsByMeetingAndCrewMember(Meeting meeting, CrewMember crewMember);

    Optional<MeetingMember> findByMeetingAndCrewMember(Meeting meeting, CrewMember crewMember);

    List<MeetingMember> findByMeeting(Meeting meeting);

    Optional<MeetingMember> findByMeetingMemberIdAndMeeting_MeetingId(Long meetingMemberId, Long meetingId);
}
