package site.mymeetup.meetupserver.interest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

public class InterestSmallDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InterestSmallRespDto {
        private long interestSmallId;
        private String name;

        @Builder
        public InterestSmallRespDto(InterestSmall interestSmall) {
            this.interestSmallId = interestSmall.getInterestSmallId();
            this.name = interestSmall.getName();
        }
    }

}
