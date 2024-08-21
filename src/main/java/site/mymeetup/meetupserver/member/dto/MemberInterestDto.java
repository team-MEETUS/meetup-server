package site.mymeetup.meetupserver.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @NotNull
        private Long memberId; // 회원 ID
        @Size(max = 5, message = "관심사는 최대 5개까지 가능합니다.")
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
