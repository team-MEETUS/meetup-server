package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewLike;
import site.mymeetup.meetupserver.member.entity.Member;

public interface CrewLikeRepository extends JpaRepository<CrewLike, Long> {

    CrewLike findByCrewAndMember(Crew crew, Member member);

    boolean existsByCrewAndMember(Crew crew, Member member);

}
