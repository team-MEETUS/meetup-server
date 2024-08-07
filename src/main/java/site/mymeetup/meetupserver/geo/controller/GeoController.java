package site.mymeetup.meetupserver.geo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.mymeetup.meetupserver.geo.service.GeoService;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.util.List;

import static site.mymeetup.meetupserver.geo.dto.GeoDto.GeoSelectRespDto;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GeoController {
    private final GeoService geoService;

    // 전체 지역 조회
    @GetMapping("/geos")
    public ApiResponse<List<GeoSelectRespDto>> getAllGeo() {
        return ApiResponse.success(geoService.getAllGeo());
    }
}
