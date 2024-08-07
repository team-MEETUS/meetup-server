package site.mymeetup.meetupserver.interest.service;

import static site.mymeetup.meetupserver.interest.dto.InterestBigDto.InterestBigSelectRespDto;
import static site.mymeetup.meetupserver.interest.dto.InterestSmallDto.InterestSmallSelectRespDto;

import java.util.List;

public interface InterestService {
    List<InterestBigSelectRespDto> getAllInterestBig();

    List<InterestSmallSelectRespDto> getAllInterestSmallByInterestBigId(Long interestBigId);
}
