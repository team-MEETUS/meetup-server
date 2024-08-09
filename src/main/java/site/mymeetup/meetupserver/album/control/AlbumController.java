package site.mymeetup.meetupserver.album.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.album.service.AlbumService;
import site.mymeetup.meetupserver.response.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class AlbumController {
    private final AlbumService albumService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{crewId}/albums")
    public ApiResponse<List<AlbumSaveRespDto>> createAlbum(@PathVariable Long crewId,
                                                           @RequestParam("images") List<MultipartFile> images) {
        return ApiResponse.success(albumService.createAlbum(crewId, images));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/albums")
    public ApiResponse<List<AlbumRespDto>> getAlbumByCrewId (@PathVariable Long crewId) {
        return ApiResponse.success(albumService.getAlbumByCrewId(crewId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{crewId}/albums/{albumId}")
    public ApiResponse<AlbumRespDto> getAlbumByCrewIdAndAlbumId (@PathVariable Long crewId,
                                                             @PathVariable Long albumId) {
        return ApiResponse.success(albumService.getAlbumByCrewIdAndAlbumId(crewId, albumId));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{crewId}/albums/{albumId}")
    public ApiResponse<?> deleteAlbum(@PathVariable Long crewId, @PathVariable Long albumId) {
        albumService.deleteAlbum(crewId, albumId);
        return ApiResponse.success(null);
    }
}
