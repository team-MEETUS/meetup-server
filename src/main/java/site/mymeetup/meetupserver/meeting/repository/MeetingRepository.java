package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.meeting.entity.Meeting;

import java.time.LocalDateTime;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    int countByCrew_CrewIdAndDateAfter(Long crewId, LocalDateTime now);
}
