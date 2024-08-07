package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.util.List;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    CrewMember findByCrew_CrewIdAndMember_MemberId(Long crewId, Long memberId);

    List<CrewMember> findByCrew_CrewIdAndStatusInOrderByStatusDesc(Long crewId, List<Integer> statuses);

    List<CrewMember> findByCrew_CrewIdAndStatus(Long crewId, int status);
}
