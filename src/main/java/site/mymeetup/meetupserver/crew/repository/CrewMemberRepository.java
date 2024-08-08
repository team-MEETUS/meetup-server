package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {

    Optional<CrewMember> findByCrew_CrewIdAndMember_MemberId(Long crewId, Long memberId);

    List<CrewMember> findByCrew_CrewIdAndRoleInOrderByRoleDesc(Long crewId, List<CrewMemberRole> roles);

    List<CrewMember> findByCrew_CrewIdAndRole(Long crewId, CrewMemberRole role);

    boolean existsByCrewAndMemberAndRole(Crew crew, Member member, CrewMemberRole role);
}