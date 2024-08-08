package site.mymeetup.meetupserver.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveReqDto;
import static site.mymeetup.meetupserver.board.dto.CommentDto.CommentSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.CommentDto.CommentSaveReqDto;

import site.mymeetup.meetupserver.board.dto.CommentDto;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.board.entity.Comment;
import site.mymeetup.meetupserver.board.repository.BoardRepository;
import site.mymeetup.meetupserver.board.repository.CommentRepository;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3ImageService s3ImageService;
    private final CommentRepository commentRepository;

    // 게시글 등록
    @Override
    public BoardSaveRespDto createBoard(Long crewId, BoardSaveReqDto boardSaveReqDto) {
        // crewId로 Crew 객체 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(boardSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // crewMember 일반인 경우 공지 예외 처리
        if (boardSaveReqDto.getCategory().equals("공지") && crewMember.getRole() == CrewMemberRole.MEMBER) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // dto -> entity
        Board board = boardRepository.save(boardSaveReqDto.toEntity(crew, crewMember));
        return BoardSaveRespDto.builder().board(board).build();
    }

    // 게시글 이미지 저장
    @Override
    public List<String> uploadImage(MultipartFile[] images) {
        return Arrays.stream(images)
                .filter(image -> !image.isEmpty())
                .map(s3ImageService::upload)
                .toList();
    }

    // 게시글 수정
    @Override
    public BoardSaveRespDto updateBoard(Long crewId, Long boardId, BoardSaveReqDto boardSaveReqDto) {
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(boardSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // 해당 게시글이 존재하는지 검증
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 요청된 모임의 게시글인지 검증
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }

        // 작성자와 요청자가 일치하는지 검증
        if (!board.getCrewMember().getCrewMemberId().equals(crewMember.getCrewMemberId())) {
            throw new CustomException(ErrorCode.BOARD_WRITER_ACCESS_DENIED);
        }

        // Board 객체 업데이트
        board.updateBoard(boardSaveReqDto.toEntity(crew, crewMember));

        // crewMember 권한 검증
        if (board.getCrewMember().getRole() == CrewMemberRole.MEMBER && board.getCategory().equals("공지")) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // DB 수정
        Board updateBoard = boardRepository.save(board);

        return BoardSaveRespDto.builder().board(updateBoard).build();
    }

    // 게시글 목록 전체 조회
    @Override
    public List<BoardRespDto> getBoardByCrewId(Long crewId) {
        List<Board> boardList = boardRepository.findBoardByCrewCrewId(crewId);

        return boardList.stream()
                .filter(board -> board.getStatus() != 0)
                .map(BoardRespDto::new)
                .toList();
    }

    // 카테고리별 게시글 목록 조회
    @Override
    public List<BoardRespDto> getBoardBYCrewIdAndCategory(Long crewId, String category) {
        if (!category.equals("공지") && !category.equals("모임후기") && !category.equals("가입인사") && !category.equals("자유")) {
            throw new CustomException(ErrorCode.BOARD_CATEGORY_NOT_FOUND);
        }

        List<Board> boardList = boardRepository.findBoardByCrew_CrewIdAndCategory(crewId, category);

        return boardList.stream()
                .filter(board -> board.getStatus() != 0)
                .map(BoardRespDto::new)
                .toList();
    }

    // 특정 게시글 조회
    @Override
    public BoardRespDto getBoardByBoardId(Long crewId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        } else if (board.getStatus() == 0) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        // 조회수 증가
        board.updateBoardHit(board.getHit()+1);
        // DB 수정
        boardRepository.save(board);

        return BoardRespDto.builder().board(board).build();
    }

    // 게시글 삭제
    @Override
    public void deleteBoard(Long crewId, Long boardId, Long crewMemberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }

        if (!board.getCrewMember().getCrewMemberId().equals(crewMemberId) && board.getCrewMember().getRole() != CrewMemberRole.ADMIN && board.getCrewMember().getRole() != CrewMemberRole.LEADER) {
            throw new CustomException(ErrorCode.BOARD_DELETE_ACCESS_DENIED);
        }

        // 삭제할 게시글 상태값 변경
        board.deleteBoard(0);
        // DB 수정
        boardRepository.save(board);
    }

    // 댓글 등록
    @Override
    public CommentSaveRespDto createComment(Long crewId, Long boardId, CommentSaveReqDto commentSaveReqDto) {
        // boardId로 Board 객체 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(commentSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // 모임에 가입된 모임원 확인 및 role 확인
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (crewMember.getRole() == CrewMemberRole.EXPELLED || crewMember.getRole() == CrewMemberRole.PENDING || crewMember.getRole() == CrewMemberRole.REJECTED) {
            throw new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND);
        }

        // dto -> Entity
        Comment comment = commentRepository.save(commentSaveReqDto.toEntity(board, crewMember));
        return CommentSaveRespDto.builder().comment(comment).build();
    }

    // 댓글 수정
    @Override
    public CommentSaveRespDto updateComment(Long crewId, Long boardId, Long commentId, CommentSaveReqDto commentSaveReqDto) {
        // boardId로 Board 객체 조회
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(commentSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // commentId로 Comment 객체 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_COMMENT_NOT_FOUND));

        // 유효성 검사
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (crewMember.getRole() == CrewMemberRole.EXPELLED || crewMember.getRole() == CrewMemberRole.PENDING || crewMember.getRole() == CrewMemberRole.REJECTED) {
            throw new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND);
        }
        if (!comment.getCrewMember().getCrewMemberId().equals(commentSaveReqDto.getCrewMemberId())) {
            throw new CustomException(ErrorCode.BOARD_COMMENT_ACCESS_DENIED);
        }
        if (!comment.getBoard().getBoardId().equals(boardId)) {
            throw new CustomException(ErrorCode.BOARD_COMMENT_ID_ACCESS_DENIED);
        }

        comment.updateComment(commentSaveReqDto.toEntity(board, crewMember));

        // DB 수정
        commentRepository.save(comment);

        return CommentSaveRespDto.builder().comment(comment).build();
    }
}
