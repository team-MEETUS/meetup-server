package site.mymeetup.meetupserver.geo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.mymeetup.meetupserver.geo.service.GeoService;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GeoController {
    private final GeoService geoService;

    @GetMapping("/geos")
    public ApiResponse<?> getAllGeo() {
        return ApiResponse.success(geoService.getAllGeo());
    }
}
