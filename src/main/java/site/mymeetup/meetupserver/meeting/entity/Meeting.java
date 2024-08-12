package site.mymeetup.meetupserver.meeting.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "meeting")
@Entity
public class Meeting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String loc;

    private String url;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private int max;

    @Column(nullable = false)
    private int attend;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private String originalImg;

    @Column(nullable = false)
    private String saveImg;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "crew_and_member_id")
    private CrewMember crewMember;

    // updateMeeting
    public void updateMeeting(Meeting meeting) {
        if (meeting.getName() != null) {
            this.name = meeting.getName();
        }
        if (meeting.getLoc() != null) {
            this.loc = meeting.getLoc();
        }
        if (meeting.getUrl() != null) {
            this.url = meeting.getUrl();
        }
        if (meeting.getPrice() != null) {
            this.price = meeting.getPrice();
        }
        if (meeting.getMax() != 0) {
            this.max = meeting.getMax();
        }
    }

    // deleteMeeting
    public void deleteMeeting(int status) {
        this.status = status;
    }

    // updateAttend
    public void changeAttend(int value) {
        this.attend += value;
    }
}
