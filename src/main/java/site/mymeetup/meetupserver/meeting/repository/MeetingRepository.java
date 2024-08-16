package site.mymeetup.meetupserver.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.meeting.entity.Meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    // 현재 진행 중인 정모 갯수
    int countByCrew_CrewIdAndStatusAndDateAfter(Long crewId, int status, LocalDateTime now);

    // 특정 모임의 특정 정모 찾기
    Optional<Meeting> findByMeetingIdAndCrewAndStatus(Long meetingId, Crew crew, int status);

    // 현재 진행 중인 정모 및 지난 정모
    @Query("SELECT m FROM Meeting m " +
            "JOIN MeetingMember mm ON m = mm.meeting " +
            "WHERE m.crew.crewId = :crewId AND m.status = :status " +
            "AND ((:isUpcoming = true AND m.date > :now) " +
            "OR (:isUpcoming = false AND m.date < :now)) " +
            "ORDER BY CASE WHEN :isUpcoming = true THEN m.date END ASC, " +
            "CASE WHEN :isUpcoming = false THEN m.date END DESC")
    List<Meeting> findMeetingsWithMembers(Long crewId, int status, LocalDateTime now, boolean isUpcoming);
}
