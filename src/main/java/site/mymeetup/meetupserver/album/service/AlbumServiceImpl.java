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
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
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

        Long memberId = 101L; // JWT를 통해서 받아온 로그인한 memberId

        // 로그인한 유저 crewMemberId로 CrewAndMember 객체 조회
        CrewMember crewMember =  crewMemberRepository.findByCrew_CrewIdAndMember_MemberId(crewId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

//        // crewMember 일반인 등록 금지
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
        List<Album> albumList = albumRepository.findAlbumByCrewCrewIdAndStatus(crewId, 1);

        return albumList.stream()
                .filter(album -> album.getStatus() != 0)
                .map(AlbumRespDto::new)
                .toList();
    }

    // 사진첩 상세 조회
    @Override
    public AlbumRespDto getAlbumByCrewIdAndAlbumId(Long crewId, Long albumId) {
        Album album = albumRepository.findAlbumByCrewCrewIdAndAlbumId(crewId, albumId);

        // 조회한 결과가 없다면 에러
        if(album == null) {
            throw new CustomException(ErrorCode.ALBUM_NOT_FOUND);
        }

        return AlbumRespDto.builder().album(album).build();
    }

    // 사진첩 삭제
    @Override
    public void deleteAlbum(Long crewId, Long albumId) {
        // 해당 사진첩이 존재하는지 검증
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALBUM_NOT_FOUND));

        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 현재 로그인한 사용자 정보 가져오기
        Long memberId = 101L; // JWT를 통해서 받아온 로그인한 memberId
        CrewMember crewNember =  crewMemberRepository.findByCrew_CrewIdAndMember_MemberId(crewId, memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // 로그인한 유저가 삭제할 권한(모임장 or 관리자 or 작성자)이 있는지 확인
        if(album.getCrewMember().getCrewMemberId() != crewNember.getCrewMemberId()
        || crewMemberRepository.existsByCrewAndMemberAndRole(crew, crewNember.getMember(), CrewMemberRole.LEADER)
        || crewMemberRepository.existsByCrewAndMemberAndRole(crew, crewNember.getMember(), CrewMemberRole.ADMIN)) {
            throw new CustomException(ErrorCode.ALBUM_DELETE_ACCESS_DENIED);
        }
        
        // 사진첩의 상태값이 삭제된 상태인지 확인
        if(album.getStatus() == 0) { // status가 0인 경우 삭제된 사진첩
            throw new CustomException(ErrorCode.ALBUM_NOT_FOUND);
        }
        
        // 사진첩의 crewId와 현재 위치하고 있는 crewId가 같은지 확인
        if(!album.getCrew().getCrewId().equals(crewId)) {
            throw new CustomException(ErrorCode.ALBUM_CREW_ACCESS_DENIED);
        }

        // 실제로 삭제하지않고 상태값 변경
        album.deleteAlbum(0);
        albumRepository.save(album);
    }
}
