package site.mymeetup.meetupserver.geo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.geo.dto.GeoDto;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {
    private final GeoRepository geoRepository;

    @Override
    public List<GeoDto.GeoRespDto> getAllGeo() {
        List<Geo> geos = geoRepository.findAll();
        return geos.stream()
                .map(GeoDto.GeoRespDto::new)
                .collect(Collectors.toList());
    }
}
