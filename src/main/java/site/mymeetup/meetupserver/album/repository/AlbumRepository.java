package site.mymeetup.meetupserver.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.album.entity.Album;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAlbumByCrewCrewId(Long crewId);
}
