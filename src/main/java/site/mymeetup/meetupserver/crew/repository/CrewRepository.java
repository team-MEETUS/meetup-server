package site.mymeetup.meetupserver.crew.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // 모임 검색
    @Query("SELECT c FROM Crew c " +
            "WHERE (:city IS NULL OR c.geo.city = :city) " +
            "AND (" +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.interestBig.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.interestSmall.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") " +
            "AND c.status = 1")
    List<Crew> searchCrews(@Param("keyword") String keyword,
                           @Param("city") String city,
                           Pageable pageable);
}
