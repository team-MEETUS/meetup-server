package site.mymeetup.meetupserver.member.service;



import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.util.List;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSMSRespDto;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSMSReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberInterestDto.MemberInterestSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberInterestDto.MemberInterestSaveReqDto;

public interface MemberService {
    MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto);

    MemberInfoDto getMemberInfo(CustomUserDetails userDetails);

    MemberUpdateRespDto updateMember(Long memberId, MemberUpdateReqDto memberUpdateReqDto,
                                     MultipartFile image, CustomUserDetails userDetails);

    void deleteMember(Long memberId, CustomUserDetails userDetails);

    MemberSelectRespDto getMemberByMemberId(Long memberId);

    MemberSMSRespDto sendSMS(MemberSMSReqDto memberSMSReqDto);

    List<MemberInterestSaveRespDto> createMemberInterests(Long memberId, List<Long> interestSmallIds, CustomUserDetails userDetails);

    List<MemberInterestSaveRespDto> updateMemberInterests(Long memberId, List<Long> interestSmallIds, CustomUserDetails userDetails);
}

