package site.mymeetup.meetupserver.geo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.geo.entity.Geo;

import java.util.List;
import java.util.Optional;

public interface GeoRepository extends JpaRepository<Geo, Long> {
    Optional<Geo> findFirstByCity(String city);

    boolean existsByCity(String city);
}
