package site.mymeetup.meetupserver.memberAndInterest.service;

import java.util.List;
import site.mymeetup.meetupserver.memberAndInterest.dto.MemberAndInterestDto;

public interface MemberAndInterestService {
    List<MemberAndInterestDto.MemberAndInterestRespDto> getAllMemberAndInterest();
}
