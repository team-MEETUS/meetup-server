package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    // id & status 값으로 특정 모임 조회
    Optional<Crew> findByCrewIdAndStatus(Long crewId, int status);

    // interestBigId 값으로 특정 모임 조회
    Page<Crew> findAllByInterestBigAndStatus(InterestBig interestBig, int status, Pageable pageable);

    // interestSmallId 값으로 특정 모임 조회
    Page<Crew> findAllByInterestSmallAndStatus(InterestSmall interestSmall, int status, Pageable pageable);

    // city & interestBigId 값으로 특정 모임 조회
    Page<Crew> findAllByGeo_CityAndInterestBigAndStatus(String city, InterestBig interestBig, int status, Pageable pageable);

    // city & interestSmallId 값으로 특정 모임 조회
    Page<Crew> findAllByGeo_CityAndInterestSmallAndStatus(String city, InterestSmall interestSmall, int status, Pageable pageable);
}
