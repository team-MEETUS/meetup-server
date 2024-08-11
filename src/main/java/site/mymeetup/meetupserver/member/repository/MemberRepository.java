package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    // 핸드폰 번호로 멤버 존재 여부 확인
    boolean existsByPhone(String phone);

    // 핸드폰 번호로 멤버 조회
    Member findByPhone(String phone);

    // id와 상태값으로 멤버 조회
    Optional<Member> findByMemberIdAndStatus(Long memberId, int status);

}