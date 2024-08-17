package site.mymeetup.meetupserver.album.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import static site.mymeetup.meetupserver.album.dto.AlbumLikeRespDto.AlbumLikeSaveRespDto;

import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSelectRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import java.util.List;

public interface AlbumService {

    List<AlbumSaveRespDto> createAlbum(Long crewId, List<MultipartFile> images, CustomUserDetails userDetails);

    List<AlbumSelectRespDto> getAlbumByCrewId(Long crewId);

    AlbumSelectRespDto getAlbumByCrewIdAndAlbumId(Long crewId, Long albumId, CustomUserDetails userDetails);

    void deleteAlbum(Long crewId, Long albumId, CustomUserDetails userDetails);

    boolean isLikeAlbum(Long crewId, Long albumId, CustomUserDetails userDetails);

    AlbumLikeSaveRespDto likeAlbum(Long crewId, Long albumId, CustomUserDetails userDetails);
}
