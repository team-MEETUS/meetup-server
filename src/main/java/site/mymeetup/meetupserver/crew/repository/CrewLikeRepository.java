package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewLike;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.List;

public interface CrewLikeRepository extends JpaRepository<CrewLike, Long> {

    CrewLike findByCrewAndMember(Crew crew, Member member);

    boolean existsByCrewAndMember(Crew crew, Member member);

    @Query("SELECT cl.crew FROM CrewLike cl WHERE cl.member = :member ORDER BY cl.crewLikeId DESC")
    List<Crew> findCrewsByMember(@Param("member") Member member);
}
