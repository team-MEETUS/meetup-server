package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // 핸드폰 번호로 멤버 조회
    @Query("SELECT m FROM Member m WHERE m.phone = :phone")
    Member findByPhone(String phone);

    // id와 상태값으로 멤버 조회
    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId AND m.status = :status")
    Optional<Member> findByMemberIdAndStatus(Long memberId, int status);

}