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

    @Override
    public BoardDto.BoardSaveRespDto createBoard(Long crewId, BoardDto.BoardSaveReqDto boardSaveReqDto) {
        // crewId로 Crew 객체 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // crewAndMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(boardSaveReqDto.getCrewMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // dto -> entity
        Board board = boardRepository.save(boardSaveReqDto.toEntity(crew, crewMember));
        return BoardDto.BoardSaveRespDto.builder().board(board).build();
    }

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
}
