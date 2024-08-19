package site.mymeetup.meetupserver.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveReqDto;
import static site.mymeetup.meetupserver.board.dto.CommentDto.CommentSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.CommentDto.CommentSaveReqDto;
import static site.mymeetup.meetupserver.board.dto.CommentDto.CommentRespDto;

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
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3ImageService s3ImageService;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    // 게시글 등록
    @Override
    public BoardSaveRespDto createBoard(Long crewId, BoardSaveReqDto boardSaveReqDto, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());
        // crewId로 Crew 객체 조회
        Crew crew = validCrew(crewId);

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

        // Board 카테고리 검증
        if (!(boardSaveReqDto.getCategory().equals("공지") || boardSaveReqDto.getCategory().equals("모임후기") || boardSaveReqDto.getCategory().equals("가입인사") || boardSaveReqDto.getCategory().equals("자유"))) {
            throw new CustomException(ErrorCode.BOARD_CATEGORY_NOT_FOUND);
        }

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
        try {
            List<String> uploadedImageUrls = Arrays.stream(images)
                    .filter(image -> !image.isEmpty())
                    .map(s3ImageService::upload)
                    .toList();

            return uploadedImageUrls;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BOARD_IMAGE_EXCEPTION);
        }
    }

    // 게시글 수정
    @Override
    public BoardSaveRespDto updateBoard(Long crewId, Long boardId, BoardSaveReqDto boardSaveReqDto, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());
        // 해당 모임이 존재하는지 검증
        Crew crew = validCrew(crewId);

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

        // 해당 게시글이 존재하는지 검증
        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 요청된 모임의 게시글인지 검증
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }

        // 공지글에 대한 권한 검증
        if (board.getCategory().equals("공지")) {
            if (crewMember.getRole() != CrewMemberRole.ADMIN && crewMember.getRole() != CrewMemberRole.LEADER) {
                throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
            }
        }

        // 작성자와 요청자가 일치하는지 검증
        if (!board.getCategory().equals("공지") && !board.getCrewMember().getCrewMemberId().equals(crewMember.getCrewMemberId())) {
            throw new CustomException(ErrorCode.BOARD_WRITER_ACCESS_DENIED);
        }

        // Board 객체 업데이트
        board.updateBoard(boardSaveReqDto.toEntity(crew, crewMember));

        // crewMember 권한 검증
        if (crewMember.getRole() == CrewMemberRole.MEMBER && board.getCategory().equals("공지")) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // DB 수정
        Board updateBoard = boardRepository.save(board);

        return BoardSaveRespDto.builder().board(updateBoard).build();
    }

    // 게시글 목록 전체 조회
    @Override
    public List<BoardRespDto> getBoardByCrewId(Long crewId, String category, int page) {
        if (page < 0) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Page<Board> boardList = null;
        if (category == null || category.isEmpty()) {
            boardList = boardRepository.findBoardByCrew_CrewIdAndStatusNotWithCustomSort(crewId, 0, PageRequest.of(page, 20));
        } else {
            if (!category.equals("공지") && !category.equals("모임후기") && !category.equals("가입인사") && !category.equals("자유")) {
                throw new CustomException(ErrorCode.BOARD_CATEGORY_NOT_FOUND);
            }
            boardList = boardRepository.findBoardByCrew_CrewIdAndCategoryAndStatusNotWithCustomSort(crewId, category, 0, PageRequest.of(page, 20));
        }

        return boardList.stream()
                .map(BoardRespDto::new)
                .toList();
    }

    // 특정 게시글 조회
    @Override
    public BoardRespDto getBoardByBoardId(Long crewId, Long boardId, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

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
    public void deleteBoard(Long crewId, Long boardId, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }

        if (!board.getCrewMember().getCrewMemberId().equals(crewMember.getCrewMemberId()) && crewMember.getRole() != CrewMemberRole.ADMIN && crewMember.getRole() != CrewMemberRole.LEADER) {
            throw new CustomException(ErrorCode.BOARD_DELETE_ACCESS_DENIED);
        }

        // 삭제할 게시글 상태값 변경
        board.deleteBoard(0);
        // DB 수정
        boardRepository.save(board);
    }

    // 게시글 고정
    @Override
    public BoardSaveRespDto updateBoardStatus(Long crewId, Long boardId, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        CrewMember crewMember = validCrewMember(crew, member);

        if (crewMember.getRole() != CrewMemberRole.ADMIN && crewMember.getRole() != CrewMemberRole.LEADER) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }

        board.updateBoardStatus(2);
        boardRepository.save(board);

        return BoardSaveRespDto.builder().board(board).build();
    }

    // 댓글 등록
    @Override
    public CommentSaveRespDto createComment(Long crewId, Long boardId, CommentSaveReqDto commentSaveReqDto, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        // boardId로 Board 객체 조회
        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

        // 모임에 가입된 모임원 확인 및 role 확인
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (crewMember.getRole() == CrewMemberRole.EXPELLED || crewMember.getRole() == CrewMemberRole.PENDING || crewMember.getRole() == CrewMemberRole.DEPARTED) {
            throw new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND);
        }

        // dto -> Entity
        Comment comment = commentRepository.save(commentSaveReqDto.toEntity(board, crewMember));
        board.updateBoardTotalComment(board.getTotalComment() + 1);
        boardRepository.save(board);
        return CommentSaveRespDto.builder().comment(comment).build();
    }

    // 댓글 수정
    @Override
    public CommentSaveRespDto updateComment(Long crewId, Long boardId, Long commentId, CommentSaveReqDto commentSaveReqDto, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        // boardId로 Board 객체 조회
        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);

        // commentId로 Comment 객체 조회
        Comment comment = commentRepository.findByBoard_BoardIdAndCommentId(boardId, commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_COMMENT_NOT_FOUND));

        // 유효성 검사
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (crewMember.getRole() == CrewMemberRole.EXPELLED || crewMember.getRole() == CrewMemberRole.PENDING || crewMember.getRole() == CrewMemberRole.DEPARTED) {
            throw new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND);
        }
        if (!comment.getCrewMember().getCrewMemberId().equals(crewMember.getCrewMemberId())) {
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

    @Override
    public void deleteComment(Long crewId, Long boardId, Long commentId, CustomUserDetails userDetails) {
        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        // boardId로 Board 객체 조회
        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        // crewMemberId로 CrewMember 객체 조회
        CrewMember crewMember = validCrewMember(crew, member);
        // commentId로 Comment 객체 조회
        Comment comment = commentRepository.findByBoard_BoardIdAndCommentId(boardId, commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_COMMENT_NOT_FOUND));

        // 유효성 검사
        if (!board.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (!comment.getBoard().getBoardId().equals(boardId)) {
            throw new CustomException(ErrorCode.BOARD_COMMENT_ID_ACCESS_DENIED);
        }
        if (!crewMember.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.BOARD_CREW_ACCESS_DENIED);
        }
        if (crewMember.getRole() != CrewMemberRole.ADMIN && crewMember.getRole() != CrewMemberRole.LEADER && !comment.getCrewMember().getCrewMemberId().equals(crewMember.getCrewMemberId())) {
            throw new CustomException(ErrorCode.BOARD_DELETE_ACCESS_DENIED);
        }

        // status 변경
        comment.deleteComment(0);
        // BoardTotalComment 변경
        board.updateBoardTotalComment(board.getTotalComment() - 1);
        boardRepository.save(board);
        // DB 수정
        commentRepository.save(comment);
    }

    @Override
    public List<CommentRespDto> getCommentByBoardId(Long crewId, Long boardId, CustomUserDetails userDetails, int page) {
        if (page < 0) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Page<Comment> commentList = null;

        Member member = validMember(userDetails.getMemberId());

        Crew crew = validCrew(crewId);

        CrewMember crewMember = validCrewMember(crew, member);

        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        commentList = commentRepository.findCommentByBoard_BoardIdAndStatus(boardId, 1, PageRequest.of(page, 20, Sort.by(Sort.Direction.ASC, "createDate")));

        return commentList.stream()
                .map(CommentRespDto::new)
                .toList();
    }

    private Member validMember(Long memberId) {
        return memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Crew validCrew(Long crewId) {
        return crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
    }

    private CrewMember validCrewMember(Crew crew, Member member) {
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        return crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));
    }

}
