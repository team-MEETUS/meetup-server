package site.mymeetup.meetupserver.interest.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "interest_small")
@Entity
public class InterestSmall {
    @Id
    private Long interestSmallId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "interest_big_id")
    private InterestBig interestBig;
}
