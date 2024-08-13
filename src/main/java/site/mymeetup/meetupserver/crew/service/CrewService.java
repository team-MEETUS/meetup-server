package site.mymeetup.meetupserver.crew.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.dto.CrewMemberDto;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewLikeDto.CrewLikeSaveRespDto;

import java.util.List;

public interface CrewService {

    CrewSaveRespDto createCrew(CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails);

    CrewSaveRespDto updateCrew(Long crewId, CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails);

    void deleteCrew(Long crewId, CustomUserDetails userDetails);

    CrewSelectRespDto getCrewByCrewId(Long crewId);

    CrewMemberSaveRespDto signUpCrew(Long crewId, CustomUserDetails userDetails);

    List<CrewSelectRespDto> getAllCrewByInterest(String city, Long interestBigId, Long interestSmallId, int page);

    List<CrewMemberSelectRespDto> getCrewMemberByCrewId(Long crewId);

    List<CrewMemberSelectRespDto> getSignUpMemberByCrewId(Long crewId, CustomUserDetails userDetails);

    CrewMemberSaveRespDto updateRole(Long crewId, CrewMemberSaveReqDto crewMemberSaveReqDto, CustomUserDetails userDetails);

    void likeCrew(Long crewId, CustomUserDetails userDetails);

    boolean isLikeCrew(Long crewId, CustomUserDetails userDetails);
}
