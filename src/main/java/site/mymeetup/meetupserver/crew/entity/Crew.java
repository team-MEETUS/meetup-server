package site.mymeetup.meetupserver.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

import java.time.LocalDateTime;
import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "crew")
@Entity
public class Crew extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long crewId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String intro;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int max;

    @Column(nullable = false)
    private int status;

    private String originalImg;

    private String saveImg;

    private LocalDateTime deadDate;

    @ManyToOne
    @JoinColumn(name = "geo_id")
    private Geo geo;

    @ManyToOne
    @JoinColumn(name = "interest_big_id")
    private InterestBig interestBig;

    @ManyToOne
    @JoinColumn(name = "interest_small_id", nullable = true)
    private InterestSmall interestSmall;

    // updateCrew
    public void updateCrew(Crew updateCrew) {
        Optional.ofNullable(updateCrew.getName()).ifPresent(name -> this.name = name);
        Optional.ofNullable(updateCrew.getIntro()).ifPresent(intro -> this.intro = intro);
        Optional.ofNullable(updateCrew.getContent()).ifPresent(content -> this.content = content);
        Optional.of(updateCrew.getMax()).ifPresent(max -> this.max = max);
        Optional.ofNullable(updateCrew.getGeo()).ifPresent(geo -> this.geo = geo);
        Optional.ofNullable(updateCrew.getInterestBig()).ifPresent(interestBig -> this.interestBig = interestBig);
        this.interestSmall = updateCrew.getInterestSmall();
        Optional.ofNullable(updateCrew.getOriginalImg()).ifPresent(originalImg -> this.originalImg = originalImg);
        Optional.ofNullable(updateCrew.getSaveImg()).ifPresent(saveImg -> this.saveImg = saveImg);
    }
}
