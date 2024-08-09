package site.mymeetup.meetupserver.meeting.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "meeting_and_member")
@Entity
public class MeetingMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_and_member_id")
    private Long meetingMemberId;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @ManyToOne
    @JoinColumn(name = "crew_and_member_id")
    private CrewMember crewMember;
}
