package site.mymeetup.meetupserver.crew.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import static site.mymeetup.meetupserver.geo.dto.GeoDto.GeoSimpleDto;
import static site.mymeetup.meetupserver.interest.dto.InterestBigDto.InterestBigSimpleDto;
import static site.mymeetup.meetupserver.interest.dto.InterestSmallDto.InterestSmallSelectRespDto;

public class CrewDto {

    @Getter
    @NoArgsConstructor
    public static class CrewSaveReqDto {
        @NotEmpty(message = "모임명은 필수 입력사항입니다.")
        @Size(max = 20, message = "모임명은 20자 이하여야 합니다.")
        private String name;
        @NotEmpty(message = "모임설명은 필수 입력사항입니다.")
        @Size(max = 1000, message = "모임설명은 1000자 이하여야 합니다.")
        private String content;
        @NotNull(message = "정원은 필수 입력사항입니다.")
        @Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
        @Max(value = 300, message = "정원은 300명 이하여야 합니다.")
        private int max;
        private String originalImg;
        private String saveImg;
        @NotNull(message = "지역은 필수 입력사항입니다.")
        private Long geoId;
        @NotNull(message = "관심사는 필수 입력사항입니다.")
        private Long interestBigId;
        private Long interestSmallId;

        public Crew toEntity(Geo geo, InterestBig interestBig, InterestSmall interestSmall, String originalImg, String saveImg) {
            return Crew.builder()
                    .name(name)
                    .intro(content.contains("\n") ? content.split("\n")[0] : content)
                    .content(content)
                    .max(max)
                    .status(1)
                    .originalImg(originalImg != null ? originalImg : "default.png")
                    .saveImg(saveImg != null ? saveImg : "/images/default.png")
                    .totalMember(1)
                    .totalLike(0)
                    .geo(geo)
                    .interestBig(interestBig)
                    .interestSmall(interestSmall)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewSaveRespDto {
        private Long crewId;

        @Builder
        public CrewSaveRespDto(Crew crew) {
            this.crewId = crew.getCrewId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewSelectRespDto {
        private Long crewId;
        private String name;
        private String intro;
        private int max;
        private String originalImg;
        private String saveImg;
        private int totalMember;
        private int totalLike;
        private GeoSimpleDto geo;
        private InterestBigSimpleDto interestBig;

        @Builder
        public CrewSelectRespDto(Crew crew) {
            this.crewId = crew.getCrewId();
            this.name = crew.getName();
            this.intro = crew.getIntro();
            this.max = crew.getMax();
            this.originalImg = crew.getOriginalImg();
            this.saveImg = crew.getSaveImg();
            this.totalLike = crew.getTotalLike();
            this.totalMember = crew.getTotalMember();
            this.geo = new GeoSimpleDto(crew.getGeo());
            this.interestBig = new InterestBigSimpleDto(crew.getInterestBig());
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewDetailRespDto {
        private Long crewId;
        private String name;
        private String intro;
        private String content;
        private int max;
        private String originalImg;
        private String saveImg;
        private int totalMember;
        private int totalLike;
        private GeoSimpleDto geo;
        private InterestBig interestBig;
        private InterestSmallSelectRespDto interestSmall;

        @Builder
        public CrewDetailRespDto(Crew crew) {
            this.crewId = crew.getCrewId();
            this.name = crew.getName();
            this.intro = crew.getIntro();
            this.content = crew.getContent();
            this.max = crew.getMax();
            this.originalImg = crew.getOriginalImg();
            this.saveImg = crew.getSaveImg();
            this.totalLike = crew.getTotalLike();
            this.totalMember = crew.getTotalMember();
            this.geo = new GeoSimpleDto(crew.getGeo());
            this.interestBig = crew.getInterestBig();
            this.interestSmall = new InterestSmallSelectRespDto(crew.getInterestSmall());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class CrewInterestReqDto {
        private Long interestBigId;
        private Long interestSmallId;
        private int page;
    }

}
