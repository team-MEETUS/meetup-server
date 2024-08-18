package site.mymeetup.meetupserver.album.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSelectRespDto;
import static site.mymeetup.meetupserver.album.dto.AlbumLikeRespDto.AlbumLikeSaveRespDto;
import site.mymeetup.meetupserver.album.entity.Album;
import site.mymeetup.meetupserver.album.entity.AlbumLike;
import site.mymeetup.meetupserver.album.repository.AlbumLikeRepository;
import site.mymeetup.meetupserver.album.repository.AlbumRepository;
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

import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveReqDto;
import static site.mymeetup.meetupserver.album.dto.AlbumDto.AlbumSaveRespDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {
    private final AlbumRepository albumRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final S3ImageService s3ImageService;
    private final AlbumLikeRepository albumLikeRepository;
    private final MemberRepository memberRepository;

    // 사진첩 등록
    @Override
    public List<AlbumSaveRespDto> createAlbum(Long crewId, List<MultipartFile> images, CustomUserDetails userDetails) {
        List<AlbumSaveRespDto> albumList = new ArrayList<>();
        AlbumSaveReqDto albumSaveReqDto = new AlbumSaveReqDto();

        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);
        // 해당 유저가 존재하는지 검증
        Member member = validateMember(userDetails);
        // 해당 유저가 모임원인지 검증
        CrewMember crewMember = validateCrewMember(crew, member);

        // 이미지가 여러개일 경우 반복해서 업로드
        for(int i = 0; i < images.size(); i++) {
            // S3 이미지 업로드
            String originalImg = null;
            String saveImg = null;
            if (!images.get(i).isEmpty()) {
                saveImg = s3ImageService.upload(images.get(i));
                originalImg = images.get(i).getOriginalFilename();
            }

            // entity로 변환하여 저장
            Album album = albumRepository.save(albumSaveReqDto.toEntity(crew, crewMember, originalImg, saveImg));
            // dto로 변환하여 List에 담기
            albumList.add(AlbumSaveRespDto.builder().album(album).build());
        }

        return albumList;
    }

    // 사진첩 조회
    @Override
    public List<AlbumSelectRespDto> getAlbumByCrewId(Long crewId, int page) {
        if (page < 0) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        List<Album> albumList = albumRepository.findAlbumByCrewCrewIdAndStatus(crewId, 1, PageRequest.of(page, 15, Sort.by(Sort.Direction.DESC, "albumId")));

        return albumList.stream()
                .map(AlbumSelectRespDto::new)
                .toList();
    }

    // 사진첩 상세 조회
    @Override
    public AlbumSelectRespDto getAlbumByCrewIdAndAlbumId(Long crewId, Long albumId, CustomUserDetails userDetails) {
        // 해당 사진첩이 존재하는지 검증
        Album album = validateAlbum(albumId);
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);
        // 해당 유저가 존재하는지 검증
        Member member = validateMember(userDetails);
        // 해당 유저가 모임원인지 검증
        CrewMember crewMember = validateCrewMember(crew, member);

        // 로그인한 유저의 role이 강퇴, 승인대기, 퇴장일 경우 조회 불가
        if (crewMember.getRole() == CrewMemberRole.EXPELLED ||
                crewMember.getRole() == CrewMemberRole.PENDING  ||
                crewMember.getRole() == CrewMemberRole.DEPARTED ) {

            throw new CustomException(ErrorCode.ALBUM_CREW_ACCESS_DENIED);
        }

        return AlbumSelectRespDto.builder().album(album).build();
    }

    // 사진첩 삭제
    @Override
    public void deleteAlbum(Long crewId, Long albumId, CustomUserDetails userDetails) {
        // 해당 사진첩이 존재하는지 검증
        Album album = validateAlbum(albumId);
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);
        // 해당 유저가 존재하는지 검증
        Member member = validateMember(userDetails);
        // 해당 유저가 모임원인지 검증
        CrewMember crewMember = validateCrewMember(crew, member);

        // 로그인한 유저가 삭제할 권한(모임장 or 관리자 or 작성자)이 있는지 확인
        if(album.getCrewMember().getCrewMemberId() != crewMember.getCrewMemberId() &&
           crewMember.getRole() != CrewMemberRole.LEADER &&
           crewMember.getRole() != CrewMemberRole.ADMIN ) {
            throw new CustomException(ErrorCode.ALBUM_DELETE_ACCESS_DENIED);
        }

        // 실제로 삭제하지않고 상태값 변경
        album.deleteAlbum(0);
        albumRepository.save(album);
    }

    // 사진첩 좋아요 여부 확인 후 삭제 or 생성
    @Override
    public AlbumLikeSaveRespDto likeAlbum(Long crewId, Long albumId, CustomUserDetails userDetails) {
        // 해당 사진첩이 존재하는지 검증
        Album album = validateAlbum(albumId);
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);
        // 해당 유저가 존재하는지 검증
        Member member = validateMember(userDetails);
        // 해당 유저가 모임원인지 검증
        CrewMember crewMember = validateCrewMember(crew, member);

        AlbumLike albumLike = albumLikeRepository.findByAlbumAndCrewMember(album, crewMember);
        AlbumLike albumLikeRtn = null;

        // 사진첩 좋아요 여부 조회
        if(!this.isLikeAlbum(crewId, albumId, userDetails)) { // 좋아요가 눌리지 않은 경우
            albumLike = AlbumLike.builder()
                    .crewMember(crewMember)
                    .album(album)
                    .build();
            album.updateAlbumTotalLike(album.getTotalLike() + 1); // 좋아요 수 증가
            albumLikeRtn = albumLikeRepository.save(albumLike);
        } else {
            albumLikeRepository.delete(albumLike);
            album.updateAlbumTotalLike(album.getTotalLike() - 1); // 좋아요 수 감소
            albumRepository.save(album);
        }

        return albumLikeRtn == null ? null : AlbumLikeSaveRespDto.builder().albumLike(albumLikeRtn).build();
    }

    // 사진첩 좋아요 여부 조회
    @Override
    public boolean isLikeAlbum(Long crewId, Long albumId, CustomUserDetails userDetails) {
        // 해당 사진첩이 존재하는지 검증
        Album album = validateAlbum(albumId);
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);
        // 해당 유저가 존재하는지 검증
        Member member = validateMember(userDetails);
        // 해당 유저가 모임원인지 검증
        CrewMember crewMember = validateCrewMember(crew, member);

        return albumLikeRepository.existsByAlbumAndCrewMember(album, crewMember);
    }

    private Member validateMember(CustomUserDetails userDetails) {
        // 해당 유저가 존재하는지 검증
        return memberRepository.findByMemberIdAndStatus(userDetails.getMemberId(), 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Crew validateCrew(Long crewId) {
        // 해당 모임이 존재하는지 검증
        return crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
    }

    private Album validateAlbum(Long albumId) {
        // 해당 사진첩이 존재하는지 검증
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new CustomException(ErrorCode.ALBUM_NOT_FOUND));
    }

    private CrewMember validateCrewMember(Crew crew, Member member) {
        // 해당 유저가 모임원인지 검증
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        return crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

    }
}
