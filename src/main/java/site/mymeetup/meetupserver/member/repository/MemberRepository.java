package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // 핸드폰 번호로 멤버 찾기
    Optional<Member> findByPhoneAndStatus(String phone, String status);

    // 카카오로 멤버 찾기
    Optional<Member> findByKakao(String kakao);

    // 네이버로 멤버 찾기
    Optional<Member> findByNaver(String naver);
}