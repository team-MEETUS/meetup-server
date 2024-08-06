package site.mymeetup.meetupserver.board.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "board")
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int hit;

    @Column(nullable = false)
    private int status;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "crew_and_member_id")
    private CrewMember crewMember;

    // updateBoard
    public void updateBoard(Board updateBoard) {
        Optional.ofNullable(updateBoard.getTitle()).ifPresent(title -> this.title = title);
        Optional.ofNullable(updateBoard.getContent()).ifPresent(content -> this.content = content);
        Optional.ofNullable(updateBoard.getCategory()).ifPresent(category -> this.category = category);
        Optional.of(updateBoard.getStatus()).ifPresent(status -> this.status = status);
    }
}
