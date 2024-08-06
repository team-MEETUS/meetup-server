package site.mymeetup.meetupserver.crew.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.dto.CrewDto;
import site.mymeetup.meetupserver.crew.dto.CrewMemberDto;

import java.util.List;

public interface CrewService {

    CrewDto.CrewSaveRespDto createCrew(CrewDto.CrewSaveReqDto crewSaveReqDto, MultipartFile image);

    CrewDto.CrewSaveRespDto updateCrew(Long crewId, CrewDto.CrewSaveReqDto crewSaveReqDto, MultipartFile image);

    void deleteCrew(Long crewId);

    CrewDto.CrewSelectRespDto getCrewByCrewId(Long crewId);

    void signUpCrew(Long crewId);

    List<CrewDto.CrewSelectRespDto> getAllCrewByInterest(String city, Long interestBigId, Long interestSmallId, int page);

    List<CrewMemberDto.CrewMemberSelectRespDto> getCrewMemberByCrewId(Long crewId);
}
