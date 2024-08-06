package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.member.dto.MemberDto;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final GeoRepository geoRepository;

    // 회원 가입
    public MemberDto.MemberSaveRespDto createMember(MemberDto.MemberSaveReqDto memberSaveReqDto) {
        // geoId로 Geo 객체 조회
        Geo geo = geoRepository.findById(memberSaveReqDto.getGeoId())
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));

        Member member = memberRepository.save(memberSaveReqDto.goEntity(geo));
        return MemberDto.MemberSaveRespDto.builder().member(member).build();
    }

    // 특정 회원 조회
    public MemberDto.MemberSelectRespDto getMemberByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        System.out.println(">>geo : " + member.getGeo());

        return MemberDto.MemberSelectRespDto.builder().member(member).build();
    }
}