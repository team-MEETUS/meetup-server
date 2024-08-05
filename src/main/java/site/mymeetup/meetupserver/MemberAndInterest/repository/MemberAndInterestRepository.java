package site.mymeetup.meetupserver.MemberAndInterest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.MemberAndInterest.entity.MemberAndInterest;

public interface MemberAndInterestRepository extends JpaRepository<MemberAndInterest, Long> {
}
