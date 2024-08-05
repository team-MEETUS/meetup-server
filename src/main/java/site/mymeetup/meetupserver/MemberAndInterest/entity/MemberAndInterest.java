package site.mymeetup.meetupserver.MemberAndInterest.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member_and_interest")
@Entity
public class MemberAndInterest {
    @Id
    private Long memberAndInterestId;

    private Long memberId;

    private Long interestSmallId;
}