package site.mymeetup.meetupserver.geo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.geo.entity.Geo;

public interface GeoRepository extends JpaRepository<Geo, Long> {
}
