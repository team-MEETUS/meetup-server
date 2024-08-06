package site.mymeetup.meetupserver.memberAndInterest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.memberAndInterest.entity.MemberAndInterest;

public interface MemberAndInterestRepository extends JpaRepository<MemberAndInterest, Long> {
}
