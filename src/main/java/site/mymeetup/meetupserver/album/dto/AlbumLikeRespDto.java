package site.mymeetup.meetupserver.album.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.album.entity.AlbumLike;

public class AlbumLikeRespDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AlbumLikeSaveRespDto {
        private Long albumLikeId;

        @Builder
        public AlbumLikeSaveRespDto(AlbumLike albumLike) {
            this.albumLikeId = albumLike.getAlbumLikeId();
        }
    }
}
