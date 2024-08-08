package site.mymeetup.meetupserver.crew.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.member.entity.Member;

public class CrewMemberDto {

    @Getter
    @NoArgsConstructor
    public static class CrewMemberSaveReqDto {
        @NotNull(message = "회원은 필수 입력사항입니다.")
        private Long memberId;
        @NotNull(message = "권한은 필수 입력사항입니다.")
        private int newRoleStatus;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewMemberSaveRespDto {
        private Long crewMemberId;

        @Builder
        public CrewMemberSaveRespDto(CrewMember crewMember) {
            this.crewMemberId = crewMember.getCrewMemberId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CrewMemberSelectRespDto {
        private Long crewMemberId;
        private CrewMemberRole role;
        private Member member;

        @Builder
        public CrewMemberSelectRespDto(CrewMember crewMember) {
            this.crewMemberId = crewMember.getCrewMemberId();
            this.role = crewMember.getRole();
            this.member = crewMember.getMember();
        }
    }

}
