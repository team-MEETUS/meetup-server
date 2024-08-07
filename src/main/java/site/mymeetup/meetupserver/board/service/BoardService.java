package site.mymeetup.meetupserver.board.service;

import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveReqDto;

import java.util.List;

public interface BoardService {

    BoardSaveRespDto createBoard(Long crewId, BoardSaveReqDto boardSaveReqDto);

    List<String> uploadImage(MultipartFile[] images);

    BoardSaveRespDto updateBoard(Long crewId, Long boardId, BoardSaveReqDto boardSaveReqDto);

    List<BoardRespDto> getBoardByCrewId(Long crewId);

    List<BoardRespDto> getBoardBYCrewIdAndCategory(Long crewId, String category);

    BoardRespDto getBoardByBoardId(Long crewId, Long boardId);

    void deleteBoard(Long crewId, Long boardId, Long crewMemberId);
}
