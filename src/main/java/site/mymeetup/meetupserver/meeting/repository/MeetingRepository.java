package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.meeting.entity.Meeting;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    int countByCrew_CrewIdAndStatusAndDateAfter(Long crewId, int status, LocalDateTime now);

    Optional<Meeting> findByMeetingIdAndStatus(Long meetingId, int status);
}
