package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;

import java.util.List;
import java.util.Optional;

public interface CrewRepository extends JpaRepository<Crew, Long> {
    // id & status 값으로 특정 모임 조회
    Optional<Crew> findByCrewIdAndStatus(Long crewId, int status);

    // interestBigId 값으로 특정 모임 조회
    Page<Crew> findAllByInterestBig_InterestBigIdAndStatus(Long interestBigId, int status, Pageable pageable);

    // interestSmallId 값으로 특정 모임 조회
    Page<Crew> findAllByInterestSmall_InterestSmallIdAndStatus(Long interestSmallId, int status, Pageable pageable);

    // city & interestBigId 값으로 특정 모임 조회
    Page<Crew> findAllByGeo_CityAndInterestBig_InterestBigIdAndStatus(String city, Long interestBigId, int status, Pageable pageable);

    // city & interestSmallId 값으로 특정 모임 조회
    Page<Crew> findAllByGeo_CityAndInterestSmall_InterestSmallIdAndStatus(String city, Long interestBigId, int status, Pageable pageable);

    // 존재하는 모임인지 검증
    boolean existsByCrewIdAndStatus(Long crewId, int status);
}
