package site.mymeetup.meetupserver.crew.service;

import org.springframework.web.multipart.MultipartFile;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;

import java.util.List;

public interface CrewService {

    CrewSaveRespDto createCrew(CrewSaveReqDto crewSaveReqDto, MultipartFile image);

    CrewSaveRespDto updateCrew(Long crewId, CrewSaveReqDto crewSaveReqDto, MultipartFile image);

    void deleteCrew(Long crewId);

    CrewSelectRespDto getCrewByCrewId(Long crewId);

    void signUpCrew(Long crewId);

    List<CrewSelectRespDto> getAllCrewByInterest(String city, Long interestBigId, Long interestSmallId, int page);

    List<CrewMemberSelectRespDto> getCrewMemberByCrewId(Long crewId);

    List<CrewMemberSelectRespDto> getSignUpMemberByCrewId(Long crewId);
}
