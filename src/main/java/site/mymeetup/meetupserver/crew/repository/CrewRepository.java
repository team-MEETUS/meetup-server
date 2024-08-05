package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    // id & status 값으로 특정 모임 조회
    Optional<Crew> findByCrewIdAndStatus(Long crewId, int status);
}
