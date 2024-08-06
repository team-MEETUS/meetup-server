package site.mymeetup.meetupserver.board.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.board.dto.BoardDto;

import java.util.List;

public interface BoardService {

    BoardDto.BoardSaveRespDto createBoard(Long crewId, BoardDto.BoardSaveReqDto boardSaveReqDto);

    List<String> uploadImage(MultipartFile[] images);

    BoardDto.BoardSaveRespDto updateBoard(Long crewId, Long boardId, BoardDto.BoardSaveReqDto boardSaveReqDto);
}
