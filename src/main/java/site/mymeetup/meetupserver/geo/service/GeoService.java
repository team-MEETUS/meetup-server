package site.mymeetup.meetupserver.geo.service;

import static site.mymeetup.meetupserver.geo.dto.GeoDto.GeoSelectRespDto;

import java.util.List;

public interface GeoService {
    List<GeoSelectRespDto> getAllGeo();
}
