package site.mymeetup.meetupserver.memberAndInterest.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.member.entity.Member;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_and_interest")
@Entity
public class MemberAndInterest extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberAndInterestId;

    @ManyToOne
    @JoinColumn(name = "member_Id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "interest_small_id", nullable = false)
    private InterestSmall interestSmall;
}