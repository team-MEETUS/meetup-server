package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {
}
