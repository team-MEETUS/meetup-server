package site.mymeetup.meetupserver.board.control;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardRespDto;
import static site.mymeetup.meetupserver.board.dto.BoardDto.BoardSaveReqDto;
import site.mymeetup.meetupserver.board.service.BoardService;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crews/{crewId}/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    // 게시글 등록
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<BoardSaveRespDto> createBoard(@PathVariable Long crewId,
                                      @RequestBody @Valid BoardSaveReqDto boardSaveReqDto) {
        return ApiResponse.success(boardService.createBoard(crewId, boardSaveReqDto));
    }

    // 게시글 수정
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{boardId}")
    public ApiResponse<BoardSaveRespDto> updateBoard(@PathVariable Long crewId,
                                      @PathVariable Long boardId,
                                      @RequestBody @Valid BoardSaveReqDto boardSaveReqDto) {
        return ApiResponse.success(boardService.updateBoard(crewId, boardId, boardSaveReqDto));
    }

    // 게시글 이미지 ajax 처리
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/images")
    public ApiResponse<List<String>> uploadImage(@RequestPart @Valid MultipartFile[] images) {
        return ApiResponse.success(boardService.uploadImage(images));
    }

    // 게시글 목록 전체 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiResponse<List<BoardRespDto>> getBoardByCrewId(@PathVariable Long crewId) {
        return ApiResponse.success(boardService.getBoardByCrewId(crewId));
    }

    // 카테고리별 게시글 목록 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{category}")
    public ApiResponse<List<BoardRespDto>> getBoardByCrewIdAndCategory(@PathVariable Long crewId,
                                                      @PathVariable String category) {
        return ApiResponse.success(boardService.getBoardBYCrewIdAndCategory(crewId, category));
    }

    // 특정 게시글 조회
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/details/{boardId}")
    public ApiResponse<BoardRespDto> getBoardOndByBoardId(@PathVariable Long crewId,
                                                          @PathVariable Long boardId) {
        return ApiResponse.success(boardService.getBoardByBoardId(crewId, boardId));
    }

    // 게시글 삭제
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/details/{boardId}/{crewMemberId}")
    public ApiResponse<?> deleteBoard(@PathVariable Long crewId,
                                      @PathVariable Long boardId,
                                      @PathVariable Long crewMemberId) {
        boardService.deleteBoard(crewId, boardId, crewMemberId);
        return ApiResponse.success(null);
    }
}
