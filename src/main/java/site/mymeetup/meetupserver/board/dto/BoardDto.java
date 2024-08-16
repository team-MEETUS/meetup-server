package site.mymeetup.meetupserver.board.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;

import java.time.LocalDateTime;

public class BoardDto {

    @Getter
    @NoArgsConstructor
    public static class BoardSaveReqDto {
        @NotNull(message = "제목은 필수 입력사항입니다.")
        @Size(max = 20, message = "제목은 20자 이하여야 합니다.")
        private String title;

        @NotNull(message = "본문은 필수 입력사항입니다.")
        @Size(max = 3000, message = "본문은 3000자 이하여야 합니다.")
        private String content;

        @NotNull(message = "카테고리는 필수 선택사항입니다.")
        private String category;

        private int status;

        public Board toEntity(Crew crew, CrewMember crewMember) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .category(category)
                    .status(status != 0 ? status : 0)
                    .crew(crew)
                    .crewMember(crewMember)
                    .build();
        }
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardSaveRespDto {
        private Long boardId;

        @Builder
        public BoardSaveRespDto(Board board) {
            this.boardId = board.getBoardId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardRespDto {
        private Long boardId;
        private String title;
        private String content;
        private String category;
        private int hit;
        private int status;
        private int totalComment;
        private CrewMemberSelectRespDto crewMember;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        @Builder
        public BoardRespDto(Board board) {
            this.boardId = board.getBoardId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.category = board.getCategory();
            this.hit = board.getHit();
            this.status = board.getStatus();
            this.totalComment = board.getTotalComment();
            this.crewMember = new CrewMemberSelectRespDto(board.getCrewMember());
            this.createDate = board.getCreateDate();
            this.updateDate = board.getUpdateDate();
        }
    }

}
