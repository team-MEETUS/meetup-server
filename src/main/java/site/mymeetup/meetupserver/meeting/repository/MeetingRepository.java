package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.meeting.entity.Meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    int countByCrew_CrewIdAndStatusAndDateAfter(Long crewId, int status, LocalDateTime now);

    Optional<Meeting> findByCrew_CrewIdAndMeetingIdAndStatus(Long crewId, Long meetingId, int status);

    List<Meeting> findByCrew_CrewIdAndStatusAndDateAfterOrderByDateAsc(Long crewId, int status, LocalDateTime now);

    List<Meeting> findByCrew_CrewIdAndStatusAndDateBeforeOrderByDateDesc(Long crewId, int status, LocalDateTime now);
}
