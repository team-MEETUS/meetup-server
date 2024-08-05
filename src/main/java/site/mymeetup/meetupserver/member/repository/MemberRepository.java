package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // 한 멤버 찾기(핸드폰 번호로)
    @Query("select m from Member m where m.phone = :phone")
    Optional<Member> getOneMemberByPhone(@Param("phone") String phone);
    // 한 멤버 찾기(카카오)
    @Query("select m from Member m where m.kakao = :kakao")
    Optional<Member> getOneMemberByKakao(@Param("kakao") String kakao);
    // 한 멤버 찾기(네이버)
    @Query("select m from Member m where m.naver = :naver")
    Optional<Member> getOneMemberByNaver(@Param("naver") String naver);
}
