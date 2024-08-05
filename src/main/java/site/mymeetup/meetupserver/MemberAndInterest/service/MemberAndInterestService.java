package site.mymeetup.meetupserver.MemberAndInterest.service;

import java.util.List;
import site.mymeetup.meetupserver.MemberAndInterest.dto.MemberAndInterestDto;

public interface MemberAndInterestService {
    List<MemberAndInterestDto.MemberAndInterestRespDto> getAllMemberAndInterest();
}
