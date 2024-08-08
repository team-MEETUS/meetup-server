package site.mymeetup.meetupserver.album.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "album")
public class Album extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;

    @Column(nullable = false)
    private int totalLike;

    @Column(nullable = false)
    private int status;

    private String originalImg;

    private String saveImg;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "crew_and_member_id")
    private CrewMember crewMember;

    // updateAlbumTotalLike
    public void updateAlbumTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    // deleteAlbum
    public void deleteAlbum(int status) {
        this.status = status;
    }
}


