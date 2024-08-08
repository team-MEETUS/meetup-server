package site.mymeetup.meetupserver.album.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.album.entity.Album;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.time.LocalDateTime;

public class AlbumDto {

    @Getter
    @NoArgsConstructor
    public static class AlbumSaveReqDto {
        private int status;

        private Long crewId;
        private String originalImg;
        private String saveImg;

        public Album toEntity(Crew crew, CrewMember crewMember, String originalImg, String saveImg) {
            return Album.builder()
                    .totalLike(0)
                    .status(1)
                    .originalImg(originalImg)
                    .saveImg(saveImg)
                    .crew(crew)
                    .crewMember(crewMember)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AlbumSaveRespDto {
        private Long albumId;

        @Builder
        public AlbumSaveRespDto(Album album) {
            this.albumId = album.getAlbumId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AlbumRespDto {
        private Long albumId;
        private int totalLike;
        private int status;
        private String originalImg;
        private String saveImg;
        private CrewMember crewMember;
        private Crew crew;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        @Builder
        public AlbumRespDto(Album album) {
            this.albumId = album.getAlbumId();
            this.totalLike = album.getTotalLike();
            this.status = album.getStatus();
            this.originalImg = album.getOriginalImg();
            this.saveImg = album.getSaveImg();
            this.crewMember = album.getCrewMember();
            this.crew = album.getCrew();
            this.createDate = album.getCreateDate();
            this.updateDate = album.getUpdateDate();
        }
    }
}
