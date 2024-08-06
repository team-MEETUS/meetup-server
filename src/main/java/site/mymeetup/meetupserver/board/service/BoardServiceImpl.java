package site.mymeetup.meetupserver.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.board.dto.BoardDto;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.board.repository.BoardRepository;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3ImageService s3ImageService;

    // 게시글 등록
    @Override
    public BoardDto.BoardSaveRespDto createBoard(Long crewId, BoardDto.BoardSaveReqDto boardSaveReqDto) {
        // crewId로 Crew 객체 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(boardSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // crewMember 일반인 경우 공지 예외 처리
        if (boardSaveReqDto.getCategory().equals("공지") && crewMember.getStatus() == 1) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // dto -> entity
        Board board = boardRepository.save(boardSaveReqDto.toEntity(crew, crewMember));
        return BoardDto.BoardSaveRespDto.builder().board(board).build();
    }

    // 게시글 이미지 저장
    @Override
    public List<String> uploadImage(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();
        String saveImg = null;

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                saveImg = s3ImageService.upload(image);
                imageUrls.add(saveImg);
            }
        }

        return imageUrls;
    }

    // 게시글 수정
    @Override
    public BoardDto.BoardSaveRespDto updateBoard(Long crewId, Long boardId, BoardDto.BoardSaveReqDto boardSaveReqDto) {
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
        if (!board.getCrewMember().getCrewAndMemberId().equals(crewMember.getCrewAndMemberId())) {
            throw new CustomException(ErrorCode.BOARD_WRITER_ACCESS_DENIED);
        }

        // Board 객체 업데이트
        board.updateBoard(boardSaveReqDto.toEntity(crew, crewMember));

        // crewMember 권한 검증
        if (board.getCrewMember().getStatus() == 1 && board.getCategory().equals("공지")) {
            throw new CustomException(ErrorCode.BOARD_ACCESS_DENIED);
        }

        // DB 수정
        Board updateBoard = boardRepository.save(board);

        return BoardDto.BoardSaveRespDto.builder().board(updateBoard).build();
    }

    // 게시글 목록 전체 조회
    @Override
    public List<BoardDto.BoardRespDto> getBoardByCrewId(Long crewId) {
        List<BoardDto.BoardRespDto> list = new ArrayList<>();
        List<Board> boardList = boardRepository.findBoardByCrewCrewId(crewId);

        for (Board board : boardList) {
            if (board.getStatus() != 0) {
                BoardDto.BoardRespDto dto = BoardDto.BoardRespDto.builder().board(board).build();
                list.add(dto);
            }
        }

        return list;
    }
}
