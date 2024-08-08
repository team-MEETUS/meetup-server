package site.mymeetup.meetupserver.board.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
@Table(name = "board_comment")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_comment_id")
    private Long commentId;

    @Column(name = "parent_board_comment_id", nullable = false)
    private int parentCommentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int status;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "crew_and_member_id")
    private CrewMember crewMember;

    // updateComment
    public void updateComment(Comment updateComment) {
        if (content != null) {
            this.content = updateComment.getContent();
        }
    }
}
