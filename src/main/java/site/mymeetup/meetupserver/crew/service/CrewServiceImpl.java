package site.mymeetup.meetupserver.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.dto.CrewDto;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.interest.repository.InterestBigRepository;
import site.mymeetup.meetupserver.interest.repository.InterestSmallRepository;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {
    private final CrewRepository crewRepository;
    private final GeoRepository geoRepository;
    private final InterestBigRepository interestBigRepository;
    private final InterestSmallRepository interestSmallRepository;
    private final S3ImageService s3ImageService;

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
        return CrewDto.CrewSaveRespDto.builder().crew(crew).build();
    }
}
