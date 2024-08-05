package site.mymeetup.meetupserver.interest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.interest.entity.InterestBig;

public class InterestBigDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InterestBigRespDto {
        private long interestBigId;
        private String name;
        private String icon;

        @Builder
        public InterestBigRespDto(InterestBig interestBig) {
            this.interestBigId = interestBig.getInterestBigId();
            this.name = interestBig.getName();
            this.icon = interestBig.getIcon();
        }
    }

}
