package site.mymeetup.meetupserver.crew.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.crew.entity.CrewLike;

public class CrewLikeDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewLikeSaveRespDto {
        private Long crewLikeId;

        @Builder
        public CrewLikeSaveRespDto(CrewLike crewLike) {
            this.crewLikeId = crewLike.getCrewLikeId();
        }
    }

}
