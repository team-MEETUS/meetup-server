package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.entity.MemberInterest;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {

    // 특정 회원의 모든 관심사 조회
    @Query("SELECT mi FROM MemberInterest mi WHERE mi.member.memberId = :memberId")
    List<MemberInterest> findMemberInterestByMember_MemberId(Long memberId);

    // 특정 회원이 특정 관심사를 가지고 있는지(중복 관심사 방지)
    @Query("SELECT CASE WHEN COUNT(mi) > 0 THEN TRUE ELSE FALSE END FROM MemberInterest mi WHERE mi.member.memberId = :memberId" +
            " AND mi.interestSmall.interestSmallId = :interestSmallId")
    boolean existsByMember_MemberIdAndInterestSmall_InterestSmallId(Long memberId, Long interestSmallId);

    // 특정 회원의 관심사 삭제
    void deleteByMember(Member member);

    }




