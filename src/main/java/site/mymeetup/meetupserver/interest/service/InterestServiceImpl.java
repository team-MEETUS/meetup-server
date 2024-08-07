package site.mymeetup.meetupserver.interest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.interest.repository.InterestBigRepository;
import site.mymeetup.meetupserver.interest.repository.InterestSmallRepository;
import static site.mymeetup.meetupserver.interest.dto.InterestBigDto.InterestBigSelectRespDto;
import static site.mymeetup.meetupserver.interest.dto.InterestSmallDto.InterestSmallSelectRespDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {
    private final InterestBigRepository interestBigRepository;
    private final InterestSmallRepository interestSmallRepository;

    @Override
    public List<InterestBigSelectRespDto> getAllInterestBig() {
        List<InterestBig> interestBigs = interestBigRepository.findAll();

        return interestBigs.stream()
                .map(InterestBigSelectRespDto::new)
                .toList();
    }

    @Override
    public List<InterestSmallSelectRespDto> getAllInterestSmallByInterestBigId(Long interestBigId) {
        // 존재하는 관심사인지 검증
        interestBigRepository.findById(interestBigId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_BIG_NOT_FOUND));

        List<InterestSmall> interestSmalls = interestSmallRepository.findAllByInterestBig_InterestBigId(interestBigId);

        return interestSmalls.stream()
                .map(InterestSmallSelectRespDto::new)
                .toList();
    }
}
