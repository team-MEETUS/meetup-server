package site.mymeetup.meetupserver.album.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.album.entity.Album;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAlbumByCrewCrewIdAndStatus(Long crewId, int status, Pageable pageable);

    Album findAlbumByCrewCrewIdAndAlbumId(Long crewId, Long albumId);
}
