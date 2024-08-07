package site.mymeetup.meetupserver.crew.dto;

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
