package site.mymeetup.meetupserver.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.dto.CrewDto;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.interest.repository.InterestBigRepository;
import site.mymeetup.meetupserver.interest.repository.InterestSmallRepository;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {
    private final CrewRepository crewRepository;
    private final GeoRepository geoRepository;
    private final InterestBigRepository interestBigRepository;
    private final InterestSmallRepository interestSmallRepository;
    private final S3ImageService s3ImageService;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    // 모임 등록
    public CrewDto.CrewSaveRespDto createCrew(CrewDto.CrewSaveReqDto crewSaveReqDto, MultipartFile image) {
        // geoId로 Geo 객체 조회
        Geo geo = geoRepository.findById(crewSaveReqDto.getGeoId())
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));

        // interestBigId로 InterestBig 객체 조회
        InterestBig interestBig = interestBigRepository.findById(crewSaveReqDto.getInterestBigId())
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_BIG_NOT_FOUND));

        // interestSmallId로 InterestSmall 객체 조회
        InterestSmall interestSmall = null;
        if (crewSaveReqDto.getInterestSmallId() != null) {
            interestSmall  = interestSmallRepository.findById(crewSaveReqDto.getInterestSmallId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_SMALL_NOT_FOUND));

            // interestSmall의 interestBig 값이 interestBig와 같은지 확인
            if (interestSmall.getInterestBig().getInterestBigId() != interestBig.getInterestBigId()) {
                throw new CustomException(ErrorCode.INTEREST_BAD_REQUEST);
            }
        }

        // S3 이미지 업로드
        String originalImg = null;
        String saveImg = null;
        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
        }

        // dto -> entity
        Crew crew = crewRepository.save(crewSaveReqDto.toEntity(geo, interestBig, interestSmall, originalImg, saveImg));

        // 모임 멤버 등록
        // 현재 로그인 한 사용자 정보 가져오기
        Long memberId = 101L;   // 테스트용
        Member member = memberRepository.findById(memberId) // 나중에 상태값까지 비교
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CrewMember crewMember = CrewMember.builder()
                .status(3)
                .crew(crew)
                .member(member)
                .build();
        crewMemberRepository.save(crewMember);

        return CrewDto.CrewSaveRespDto.builder().crew(crew).build();
    }

    // 모임 수정
    public CrewDto.CrewSaveRespDto updateCrew(Long crewId, CrewDto.CrewSaveReqDto crewSaveReqDto, MultipartFile image) {
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // geoId로 Geo 객체 조회
        Geo geo = geoRepository.findById(crewSaveReqDto.getGeoId())
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));

        // interestBigId로 InterestBig 객체 조회
        InterestBig interestBig = interestBigRepository.findById(crewSaveReqDto.getInterestBigId())
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_BIG_NOT_FOUND));

        // interestSmallId로 InterestSmall 객체 조회
        InterestSmall interestSmall = null;
        if (crewSaveReqDto.getInterestSmallId() != null) {
            interestSmall  = interestSmallRepository.findById(crewSaveReqDto.getInterestSmallId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_SMALL_NOT_FOUND));

            // interestSmall의 interestBig 값이 interestBig와 같은지 확인
            if (!interestSmall.getInterestBig().getInterestBigId().equals(interestBig.getInterestBigId())) {
                throw new CustomException(ErrorCode.INTEREST_BAD_REQUEST);
            }
        }

        // S3 이미지 업로드
        String originalImg = null;
        String saveImg = null;

        // 이미지 변경 O => 변경하는 이미지 업로드 후 DB 저장
        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
        }
        // 이미지 변경 X => 기존 이미지 그대로 가져감
        else if (crewSaveReqDto.getOriginalImg() != null && crewSaveReqDto.getSaveImg() != null) {
            if (!crewSaveReqDto.getSaveImg().equals(crew.getSaveImg()) && !crewSaveReqDto.getOriginalImg().equals(crew.getOriginalImg())) {
                throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
            }
            originalImg = crewSaveReqDto.getOriginalImg();
            saveImg = crewSaveReqDto.getSaveImg();
        }
        // 원본 이미지 또는 저장 이미지 하나만 널일 경우
        else if (crewSaveReqDto.getOriginalImg() != null || crewSaveReqDto.getSaveImg() != null) {
            throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
        }
        // 이미지 삭제 => null

        // Crew 객체 업데이트
        crew.updateCrew(crewSaveReqDto.toEntity(geo, interestBig, interestSmall, originalImg, saveImg));

        // DB 수정
        Crew updateCrew = crewRepository.save(crew);

        return CrewDto.CrewSaveRespDto.builder().crew(updateCrew).build();
    }

    // 모임 삭제
    public void deleteCrew(Long crewId) {
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
        // 삭제할 모임 상태값 변경
        crew.changeStatus(0);
        // DB 수정
        crewRepository.save(crew);
    }

    // 특정 모임 조회
    public CrewDto.CrewSelectRespDto getCrewByCrewId(Long crewId) {
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
        System.out.println(">>>>>>>>>>>" + crew.getInterestBig());
        return CrewDto.CrewSelectRespDto.builder().crew(crew).build();
    }

    // 모임 가입 신청
    @Override
    public void signUpCrew(Long crewId) {
        // 현재 로그인 한 사용자 정보 가져오기
        Long memberId = 101L;   // 테스트용
        Member member = memberRepository.findById(memberId) // 나중에 상태값까지 비교
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 모임원으로 존재하는지 검증
        CrewMember isCrewMember = crewMemberRepository.findByCrew_CrewIdAndMember_MemberId(crewId, memberId);
        if (isCrewMember != null) {
            throw new CustomException(ErrorCode.ALREADY_CREW_MEMBER);
        }

        // 모임멤버 추가
        CrewMember crewMember = CrewMember.builder()
                .status(4)
                .crew(crew)
                .member(member)
                .build();
        crewMemberRepository.save(crewMember);
    }

    // 관심사 별 모임 조회
    @Override
    public List<CrewDto.CrewSelectRespDto> getAllCrewByInterest(String city, Long interestBigId, Long interestSmallId, int page) {
        // 검증
        validateInputs(city, interestBigId, interestSmallId);

        // 페이지 번호 유효성 검사
        if (page < 0) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        // 모임 리스트 조회
        Page<Crew> crews = null;

        if (city == null) {     // 비회원
            crews = interestBigId != null
                    ? crewRepository.findAllByInterestBig_InterestBigIdAndStatus(interestBigId, 1, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "totalMember")))
                    : crewRepository.findAllByInterestSmall_InterestSmallIdAndStatus(interestSmallId, 1, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "totalMember")));
        } else {                // 회원
            crews = interestBigId != null
                    ? crewRepository.findAllByGeo_CityAndInterestBig_InterestBigIdAndStatus(city, interestBigId, 1, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "totalMember")))
                    : crewRepository.findAllByGeo_CityAndInterestSmall_InterestSmallIdAndStatus(city, interestSmallId, 1, PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "totalMember")));
        }

        return crews.stream()
                .map(CrewDto.CrewSelectRespDto::new)
                .collect(Collectors.toList());
    }

    private void validateInputs(String city, Long interestBigId, Long interestSmallId) {
        if (city != null) {
            geoRepository.findFirstByCity(city).orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));
        }
        if (interestBigId != null) {
            interestBigRepository.findById(interestBigId).orElseThrow(() -> new CustomException(ErrorCode.INTEREST_BIG_NOT_FOUND));
        }
        if (interestSmallId != null) {
            interestSmallRepository.findById(interestSmallId).orElseThrow(() -> new CustomException(ErrorCode.INTEREST_SMALL_NOT_FOUND));
        }
        if ((interestBigId == null && interestSmallId == null) || (interestBigId != null && interestSmallId != null)) {
            throw new CustomException(ErrorCode.CREW_BAD_REQUEST);
        }
    }

}
