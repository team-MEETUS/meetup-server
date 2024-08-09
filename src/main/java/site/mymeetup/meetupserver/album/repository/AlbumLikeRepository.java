package site.mymeetup.meetupserver.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.album.entity.Album;
import site.mymeetup.meetupserver.album.entity.AlbumLike;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

public interface AlbumLikeRepository extends JpaRepository<AlbumLike, Long> {
    AlbumLike findByAlbumAndCrewMember(Album album, CrewMember crewMember);

    boolean existsByAlbumAndCrewMember(Album album, CrewMember crewMember);
}
