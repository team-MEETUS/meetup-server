package site.mymeetup.meetupserver.interest.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "interest_big")
@Entity
public class InterestBig {
    @Id
    private Long interestBigId;

    private String name;

    private String icon;

    @OneToMany(mappedBy = "interestBig", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 무한 루프 방지
    private List<InterestSmall> interestSmalls;
}
