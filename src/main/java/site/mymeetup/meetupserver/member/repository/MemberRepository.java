package site.mymeetup.meetupserver.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.member.entity.Member;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // 핸드폰 번호와 상태값으로 멤버 찾기
    Optional<Member> findByPhoneAndStatus(String phone, Integer status);

}