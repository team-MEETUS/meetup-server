package site.mymeetup.meetupserver.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import site.mymeetup.meetupserver.member.entity.MemberInterest;

import java.util.List;

public class MemberInterestDto {

    // 회원 관심사 등록, 수정 req
    @Getter
    @NoArgsConstructor
    public static class MemberInterestSaveReqDto {
        private Long memberId; // 회원 ID
        private List<Long> interestSmallId; // 관심사

        @Builder
        public MemberInterestSaveReqDto(Long memberId, List<Long> interestSmallId) {
            this.memberId = memberId;
            this.interestSmallId = interestSmallId;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberInterestSaveRespDto{
        private Long memberInterestId;

        @Builder
        public MemberInterestSaveRespDto(MemberInterest memberInterest) {
            this.memberInterestId = memberInterest.getMemberInterestId();
        }
    }

}
