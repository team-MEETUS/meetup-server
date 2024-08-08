package site.mymeetup.meetupserver.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.album.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
