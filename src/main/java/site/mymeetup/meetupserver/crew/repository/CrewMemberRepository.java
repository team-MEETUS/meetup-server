package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
}
