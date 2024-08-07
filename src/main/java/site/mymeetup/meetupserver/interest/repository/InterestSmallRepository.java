package site.mymeetup.meetupserver.interest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

import java.util.List;

public interface InterestSmallRepository extends JpaRepository<InterestSmall, Long> {

    List<InterestSmall> findAllByInterestBig_InterestBigId(Long interestBigId);

    boolean existsById(Long interestSmallId);

}
