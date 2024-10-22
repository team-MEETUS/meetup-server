package site.mymeetup.meetupserver.crew.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewDetailRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewInterestReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewChatRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;

import java.util.List;

public interface CrewService {

    CrewSaveRespDto createCrew(CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails);

    CrewSaveRespDto updateCrew(Long crewId, CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails);

    void deleteCrew(Long crewId, CustomUserDetails userDetails);

    CrewDetailRespDto getCrewByCrewId(Long crewId);

    CrewMemberRole getCrewMemberRole(Long crewId, CustomUserDetails userDetails);

    CrewMemberSaveRespDto signUpCrew(Long crewId, CustomUserDetails userDetails);

    List<CrewSelectRespDto> getAllCrewByInterest(CrewInterestReqDto crewInterestReqDto, CustomUserDetails userDetails);

    List<CrewChatRespDto> getActiveCrew(int page, CustomUserDetails userDetails);

    List<CrewSelectRespDto> getNewCrew(int page, CustomUserDetails userDetails);

    List<CrewSelectRespDto> getMyCrew(CustomUserDetails userDetails);

    List<CrewSelectRespDto> getMyLikeCrew(CustomUserDetails userDetails);

    List<CrewSelectRespDto> getSearchCrew(String keyword, int page, CustomUserDetails userDetails);

    List<CrewMemberSelectRespDto> getCrewMemberByCrewId(Long crewId);

    List<CrewMemberSelectRespDto> getSignUpMemberByCrewId(Long crewId, CustomUserDetails userDetails);

    CrewMemberSaveRespDto updateRole(Long crewId, CrewMemberSaveReqDto crewMemberSaveReqDto, CustomUserDetails userDetails);

    void likeCrew(Long crewId, CustomUserDetails userDetails);

    boolean isLikeCrew(Long crewId, CustomUserDetails userDetails);
}
