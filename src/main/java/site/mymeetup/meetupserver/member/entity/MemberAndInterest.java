package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "memberAndInterest")
@Entity
public class MemberAndInterest {
    @Id
    private Long memberAndInterestId;

    private Long memberId;

    private Long interestSmallId;
}