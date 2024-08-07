package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.util.List;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    CrewMember findByCrew_CrewIdAndMember_MemberId(Long crewId, Long memberId);

    List<CrewMember> findByCrew_CrewIdAndRoleInOrderByRoleDesc(Long crewId, List<Integer> roles);

    List<CrewMember> findByCrew_CrewIdAndRole(Long crewId, int role);
}
