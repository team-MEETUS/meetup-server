package site.mymeetup.meetupserver.album.service;

import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import java.util.List;

public interface AlbumService {

    List<AlbumSaveRespDto> createAlbum(Long crewId, List<MultipartFile> images);

    List<AlbumRespDto> getAlbumByCrewId(Long crewId);
}
