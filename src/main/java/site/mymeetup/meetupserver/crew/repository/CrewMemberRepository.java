package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
    // 특정 role인 멤버 조회
    Optional<CrewMember> findByCrewAndMemberAndRoleIn(Crew crew, Member member, List<CrewMemberRole> roles);

    // 특정 모임의 권한을 가졌는지 확인 (ex. 모임장인지)
    Boolean existsByCrewAndMemberAndRole(Crew crew, Member member, CrewMemberRole role);

    // 모임원 조회
    List<CrewMember> findByCrewAndRoleInOrderByRoleDesc(Crew crew, List<CrewMemberRole> roles);

    // 특정 모임에서 승인 대기 상태의 모임원 조회
    List<CrewMember> findByCrewAndRole(Crew crew, CrewMemberRole role);

    Optional<CrewMember> findByCrew_CrewIdAndMember_MemberId(Long crewId, Long memberId);

    // 사용자가 속한 모임 조회
    @Query("SELECT cm.crew FROM CrewMember cm " +
            "WHERE cm.member = :member " +
            "AND cm.role IN (:roles) " +
            "AND cm.crew.status = 1 " +
            "ORDER BY cm.createDate DESC")
    List<Crew> findCrewsByMember(@Param("member") Member member,
                                 @Param("roles") List<CrewMemberRole> roles);
}