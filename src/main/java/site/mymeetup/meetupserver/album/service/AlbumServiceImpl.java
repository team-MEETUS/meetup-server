package site.mymeetup.meetupserver.album.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumRespDto;
import site.mymeetup.meetupserver.album.entity.Album;
import site.mymeetup.meetupserver.album.repository.AlbumRepository;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveReqDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3ImageService s3ImageService;

    // 사진첩 등록
    @Override
    public List<AlbumSaveRespDto> createAlbum(Long crewId, List<MultipartFile> images) {
        List<AlbumSaveRespDto> albumList = new ArrayList<>();

        AlbumSaveReqDto albumSaveReqDto = new AlbumSaveReqDto();
        // crewId로 Crew 객체 조회
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 로그인한 유저 crewMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember = crewMemberRepository.findById(1L) // 로그인 구현 후 수정
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

//        // crewMember 일반인 경우 접근 금지
//        if (crewMember.getRole() == CrewMemberRole.MEMBER) {
//            throw new CustomException(ErrorCode.ALBUM_ACCESS_DENIED);
//        }

        for(int i = 0; i < images.size(); i++) {
            // S3 이미지 업로드
            String originalImg = null;
            String saveImg = null;
            if (!images.get(i).isEmpty()) {
                saveImg = s3ImageService.upload(images.get(i));
                originalImg = images.get(i).getOriginalFilename();
            }

            Album album = albumRepository.save(albumSaveReqDto.toEntity(crew, crewMember, originalImg, saveImg));
            albumList.add(AlbumSaveRespDto.builder().album(album).build());
        }

        return albumList;
    }

    // 사진첩 조회
    @Override
    public List<AlbumRespDto> getAlbumByCrewId(Long crewId) {
        List<Album> albumList = albumRepository.findAlbumByCrewCrewId(crewId);

        return albumList.stream()
                .filter(album -> album.getStatus() != 0)
                .map(AlbumRespDto::new)
                .toList();
    }
}
