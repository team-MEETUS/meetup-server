package site.mymeetup.meetupserver.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "crew")
@Entity
public class Crew extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crewId;

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

    @Column(nullable = false)
    private int totalMember;

    @Column(nullable = false)
    private int totalLike;

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
        if (updateCrew.getName() != null) {
            this.name = updateCrew.getName();
        }
        if (updateCrew.getIntro() != null) {
            this.intro = updateCrew.getIntro();
        }
        if (updateCrew.getContent() != null) {
            this.content = updateCrew.getContent();
        }
        if (updateCrew.getMax() != 0) {
            this.max = updateCrew.getMax();
        }
        if (updateCrew.getGeo() != null) {
            this.geo = updateCrew.getGeo();
        }
        if (updateCrew.getInterestBig() != null) {
            this.interestBig = updateCrew.getInterestBig();
        }
        this.interestSmall = updateCrew.getInterestSmall();
        if (updateCrew.getOriginalImg() != null) {
            this.originalImg = updateCrew.getOriginalImg();
        }
        if (updateCrew.getSaveImg() != null) {
            this.saveImg = updateCrew.getSaveImg();
        }
    }

    // deleteCrew
    public void changeStatus(int status) {
        this.status = status;
    }

    // updateTotalMember
    public void changeTotalMember(int value) {
        this.totalMember += value;
    }

    // updateTotalMember
    public void changeTotalLike(int value) {
        this.totalLike += value;
    }
}
