package site.mymeetup.meetupserver.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.member.entity.Member;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "crew_like")
@Entity
public class CrewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crewLikeId;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
