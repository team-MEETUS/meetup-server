package site.mymeetup.meetupserver.MemberAndInterest.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.MemberAndInterest.entity.MemberAndInterest;

public class MemberAndInterestDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberAndInterestRespDto {
        private Long memberAndInterestId;
        private Long memberId;
        private Long interestSmallId;

        @Builder
        public MemberAndInterestRespDto(MemberAndInterest memberAndInterest) {
            this.memberAndInterestId= memberAndInterest.getMemberAndInterestId();
            this.memberId = memberAndInterest.getMemberId();
            this.interestSmallId = memberAndInterest.getInterestSmallId();
        }
    }

}