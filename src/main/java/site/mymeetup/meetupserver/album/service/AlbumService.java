package site.mymeetup.meetupserver.album.service;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveReqDto;

import java.util.List;

public interface AlbumService {

    List<AlbumSaveRespDto> createAlbum(Long crewId, List<MultipartFile> images);
}
