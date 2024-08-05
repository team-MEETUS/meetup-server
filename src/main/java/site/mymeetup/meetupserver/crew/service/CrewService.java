package site.mymeetup.meetupserver.crew.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.crew.dto.CrewDto;

public interface CrewService {

    CrewDto.CrewSaveRespDto createCrew(CrewDto.CrewSaveReqDto crewSaveReqDto, MultipartFile image);

}
