package site.mymeetup.meetupserver.geo.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.geo.entity.Geo;

public class GeoDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GeoSelectRespDto {
        private Long geoId;
        private String city;
        private String district;
        private String county;
        private Double latitude;
        private Double longitude;

        @Builder
        public GeoSelectRespDto(Geo geo) {
            this.geoId = geo.getGeoId();
            this.city = geo.getCity();
            this.district = geo.getDistrict();
            this.county = geo.getCounty();
            this.latitude = geo.getLatitude();
            this.longitude = geo.getLongitude();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GeoSimpleDto {
        private Long geoId;
        private String city;
        private String district;

        @Builder
        public GeoSimpleDto(Geo geo) {
            this.geoId = geo.getGeoId();
            this.city = geo.getCity();
            this.district = geo.getDistrict();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GeoMemberDto {
        private Long geoId;
        private String city;
        private String district;
        private String county;

        @Builder
        public GeoMemberDto(Geo geo) {
            this.geoId = geo.getGeoId();
            this.city = geo.getCity();
            this.district = geo.getDistrict();
            this.county = geo.getCounty();
        }
    }

}
