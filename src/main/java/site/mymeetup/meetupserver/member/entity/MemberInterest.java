package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_and_interest")
@Entity
public class MemberInterest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_and_interest_id")
    private Long memberInterestId;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name="interest_small_id")
    private InterestSmall interestSmall;
}
