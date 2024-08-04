package site.mymeetup.meetupserver.geo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.geo.entity.Geo;

public class GeoDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GeoRespDto {
        private Long geoId;
        private String city;
        private String district;
        private String county;
        private Double latitude;
        private Double longitude;

        @Builder
        public GeoRespDto(Geo geo) {
            this.geoId = geo.getGeoId();
            this.city = geo.getCity();
            this.district = geo.getDistrict();
            this.county = geo.getCounty();
            this.latitude = geo.getLatitude();
            this.longitude = geo.getLongitude();
        }
    }

}
