package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // id와 상태값으로 멤버 조회
    Optional<Member> findByMemberIdAndStatus(Long memberId, int status);

}