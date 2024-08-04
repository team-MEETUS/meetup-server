package site.mymeetup.meetupserver.interest.service;

import site.mymeetup.meetupserver.interest.dto.InterestBigDto;
import site.mymeetup.meetupserver.interest.dto.InterestSmallDto;

import java.util.List;

public interface InterestService {
    List<InterestBigDto.InterestBigRespDto> getAllInterestBig();

    List<InterestSmallDto.InterestSmallRespDto> getAllInterestSmallByInterestBigId(Long interestBigId);
}
