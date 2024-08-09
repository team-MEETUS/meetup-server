package site.mymeetup.meetupserver.album.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.album.dto.AlbumLikeRespDto.AlbumLikeSaveRespDto;
import site.mymeetup.meetupserver.album.service.AlbumService;
import site.mymeetup.meetupserver.response.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    // 사진첩 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/albums")
    public ApiResponse<List<AlbumSaveRespDto>> createAlbum(@PathVariable Long crewId,
                                                           @RequestParam List<MultipartFile> images) {
        return ApiResponse.success(albumService.createAlbum(crewId, images));
    }

    // 사진첩 목록 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/albums")
    public ApiResponse<List<AlbumRespDto>> getAlbumByCrewId (@PathVariable Long crewId) {
        return ApiResponse.success(albumService.getAlbumByCrewId(crewId));
    }

    // 사진첩 상세 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/albums/{albumId}")
    public ApiResponse<AlbumRespDto> getAlbumByCrewIdAndAlbumId (@PathVariable Long crewId,
                                                                 @PathVariable Long albumId) {
        return ApiResponse.success(albumService.getAlbumByCrewIdAndAlbumId(crewId, albumId));
    }

    // 사진첩 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}/albums/{albumId}")
    public ApiResponse<?> deleteAlbum(@PathVariable Long crewId, @PathVariable Long albumId) {
        albumService.deleteAlbum(crewId, albumId);
        return ApiResponse.success(null);
    }

    // 사진첩 좋아요 여부 확인 후 삭제 or 생성
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/albums/{albumId}/likes")
    public ApiResponse<AlbumLikeSaveRespDto> likeAlbum(@PathVariable Long crewId,
                                                       @PathVariable Long albumId) {
        return ApiResponse.success(albumService.likeAlbum(crewId, albumId));
    }

    // 사진첩 좋아요 여부 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/albums/{albumId}/likes")
    public ApiResponse<Boolean> isLikeCrew(@PathVariable Long crewId,
                                           @PathVariable Long albumId) {
        return ApiResponse.success(albumService.isLikeAlbum(crewId, albumId));
    }
}
