package site.mymeetup.meetupserver.board.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.board.entity.Comment;
import site.mymeetup.meetupserver.crew.entity.CrewMember;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @NoArgsConstructor
    public static class CommentSaveReqDto {
        @NotNull(message = "루트 댓글은 필수입니다.")
        private int parentCommentId;

        @NotNull(message = "내용은 필수 입력사항입니다.")
        @Size(max = 100, message = "댓글은 최대 100자입니다.")
        private String content;

        private Long boardId;
        private Long crewMemberId;

        public Comment toEntity(Board board, CrewMember crewMember) {
            return Comment.builder()
                    .parentCommentId(parentCommentId)
                    .content(content)
                    .status(1)
                    .board(board)
                    .crewMember(crewMember)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentSaveRespDto {
        private Long commentId;

        @Builder
        public CommentSaveRespDto(Comment comment) {
            this.commentId = comment.getCommentId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentRespDto {
        private Long boardCommentId;
        private Long boardId;
        private CrewMember crewMember;
        private int parentCommentId;
        private String content;
        private int status;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        @Builder
        public CommentRespDto(Comment comment) {
            this.boardCommentId = comment.getCommentId();
            this.boardId = comment.getBoard().getBoardId();
            this.crewMember = comment.getCrewMember();
            this.parentCommentId = comment.getParentCommentId();
            this.content = comment.getContent();
            this.status = comment.getStatus();
            this.createDate = comment.getCreateDate();
            this.updateDate = comment.getUpdateDate();
        }
    }
}
