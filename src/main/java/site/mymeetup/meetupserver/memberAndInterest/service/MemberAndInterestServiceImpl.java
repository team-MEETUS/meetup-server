package site.mymeetup.meetupserver.memberAndInterest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.memberAndInterest.dto.MemberAndInterestDto;
import site.mymeetup.meetupserver.memberAndInterest.entity.MemberAndInterest;
import site.mymeetup.meetupserver.memberAndInterest.repository.MemberAndInterestRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberAndInterestServiceImpl implements MemberAndInterestService {
    private final MemberAndInterestRepository memberAndInterestRepository;

    @Override
    public List<MemberAndInterestDto.MemberAndInterestRespDto> getAllMemberAndInterest(){
        List<MemberAndInterest> memberAndInterests = memberAndInterestRepository.findAll();
        return memberAndInterests.stream()
                .map(MemberAndInterestDto.MemberAndInterestRespDto::new)
                .collect(Collectors.toList());
    }
}