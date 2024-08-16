package site.mymeetup.meetupserver.meeting.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;

public class MeetingMemberDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeetingMemberRespDto {
        private Long meetingMemberId;
        private Long meetingId;
        private CrewMemberSelectRespDto crewMember;

        @Builder
        public MeetingMemberRespDto(MeetingMember meetingMember) {
            this.meetingMemberId = meetingMember.getMeetingMemberId();
            this.meetingId = meetingMember.getMeeting().getMeetingId();
            this.crewMember = new CrewMemberSelectRespDto(meetingMember.getCrewMember());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MeetingMemberReqDto {
        @NotEmpty(message = "회원은 필수 입력사항입니다.")
        private Long memberId;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeetingMemberSimpleDto {
        private Long meetingMemberId;
        private CrewMemberSelectRespDto crewMember;

        @Builder
        public MeetingMemberSimpleDto(MeetingMember meetingMember) {
            this.meetingMemberId = meetingMember.getMeetingMemberId();
            this.crewMember = new CrewMemberSelectRespDto(meetingMember.getCrewMember());
        }
    }

}
