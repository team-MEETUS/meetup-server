package site.mymeetup.meetupserver.meeting.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.meeting.entity.Meeting;
import site.mymeetup.meetupserver.meeting.entity.MeetingMember;

public class MeetingMemberDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeetingMemberRespDto {
        private Long meetingMemberId;
        private Long meetingId;
        private CrewMember crewMember;

        @Builder
        public MeetingMemberRespDto(MeetingMember meetingMember) {
            this.meetingMemberId = meetingMember.getMeetingMemberId();
            this.meetingId = meetingMember.getMeeting().getMeetingId();
            this.crewMember = meetingMember.getCrewMember();
        }
    }

}
