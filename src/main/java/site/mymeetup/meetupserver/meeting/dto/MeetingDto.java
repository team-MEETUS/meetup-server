package site.mymeetup.meetupserver.meeting.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.meeting.entity.Meeting;

import java.time.LocalDateTime;

public class MeetingDto {

    @Getter
    @NoArgsConstructor
    public static class MeetingSaveReqDto {
        @NotEmpty(message = "정모명은 필수 입력사항입니다.")
        @Size(max = 20, message = "정모명은 20자 이하여야 합니다.")
        private String name;
        @NotNull(message = "날짜는 필수 입력사항입니다.")
        private LocalDateTime date;
        @NotEmpty(message = "정모위치는 필수 입력사항입니다.")
        @Size(max = 20, message = "정모위치는 20자 이하여야 합니다.")
        private String loc;
        @Size(max = 100, message = "위치 url은 100자 이하여야 합니다.")
        private String url;
        @NotEmpty(message = "정모비용은 필수 입력사항입니다.")
        @Size(max = 20, message = "정모비용은 20자 이하여야 합니다.")
        private String price;
        @NotNull(message = "정원은 필수 입력사항입니다.")
        @Min(value = 1, message = "정원은 1명 이상이어야 합니다.")
        @Max(value = 300, message = "정원은 300명 이하여야 합니다.")
        private int max;

        public Meeting toEntity(String originalImg, String saveImg, Crew crew, CrewMember crewMember) {
            return Meeting.builder()
                    .name(name)
                    .date(date)
                    .loc(loc)
                    .url(url)
                    .price(price)
                    .max(max)
                    .attend(1)
                    .status(1)
                    .originalImg(originalImg)
                    .saveImg(saveImg)
                    .crew(crew)
                    .crewMember(crewMember)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeetingSaveRespDto {
        private Long meetingId;

        @Builder
        public MeetingSaveRespDto(Meeting meeting) {
            this.meetingId = meeting.getMeetingId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MeetingSelectRespDto {
        private Long meetingId;
        private String name;
        private LocalDateTime date;
        private String loc;
        private String url;
        private String price;
        private int max;
        private int attend;
        private String originalImg;
        private String saveImg;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;
        private Long crewId;
        private Long crewMemberId;

        @Builder
        public MeetingSelectRespDto(Meeting meeting) {
            this.meetingId = meeting.getMeetingId();
            this.name = meeting.getName();
            this.date = meeting.getDate();
            this.loc = meeting.getLoc();
            this.url = meeting.getUrl();
            this.price = meeting.getPrice();
            this.max = meeting.getMax();
            this.attend = meeting.getAttend();
            this.originalImg = meeting.getOriginalImg();
            this.saveImg = meeting.getSaveImg();
            this.createDate = meeting.getCreateDate();
            this.updateDate = meeting.getUpdateDate();
            this.crewId = meeting.getCrew().getCrewId();
            this.crewMemberId = meeting.getCrewMember().getCrewMemberId();
        }
    }

}
