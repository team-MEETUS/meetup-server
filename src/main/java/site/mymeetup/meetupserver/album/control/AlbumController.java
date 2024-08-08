package site.mymeetup.meetupserver.album.control;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveReqDto;
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

//    // 게시글 이미지 ajax 처리
//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping("/images")
//    public ApiResponse<List<String>> uploadImage(@RequestPart @Valid MultipartFile[] images) {
//        return ApiResponse.success(albumService.uploadImage(images));
//    }
}
