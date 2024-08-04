package site.mymeetup.meetupserver.geo.service;

import site.mymeetup.meetupserver.geo.dto.GeoDto;

import java.util.List;

public interface GeoService {
    List<GeoDto.GeoRespDto> getAllGeo();
}
