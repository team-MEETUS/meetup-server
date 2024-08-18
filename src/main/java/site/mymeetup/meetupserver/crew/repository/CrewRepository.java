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

    // 관심사 별 모임 조회
    @Query("SELECT c FROM Crew c " +
            "WHERE (:city IS NULL OR c.geo.city = :city) " +
            "AND (:interestBig IS NULL OR c.interestBig = :interestBig) " +
            "AND (:interestSmall IS NULL OR c.interestSmall = :interestSmall) " +
            "AND c.status = 1")
    Page<Crew> findCrewsByInterest(@Param("city") String city,
                                   @Param("interestBig") InterestBig interestBig,
                                   @Param("interestSmall") InterestSmall interestSmall,
                                   Pageable pageable);


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
