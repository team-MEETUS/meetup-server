package site.mymeetup.meetupserver.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.member.entity.Member;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "crew_and_member")
@Entity
public class CrewAndMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crewAndMemberId;

    @Column(nullable = false)
    private int status;

    @ManyToOne
    @JoinColumn(name = "crewId")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;
}
