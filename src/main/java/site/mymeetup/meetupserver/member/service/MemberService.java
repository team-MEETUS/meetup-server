package site.mymeetup.meetupserver.member.service;


import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.dto.MemberDto;
import site.mymeetup.meetupserver.member.entity.Member;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSMSRespDto;


import java.io.IOException;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSMSReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateReqDto;

public interface MemberService {
    MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto, CustomUserDetails customUserDetails);

    MemberInfoDto getMemberInfo(CustomUserDetails userDetails);

    MemberUpdateRespDto updateMember(MemberUpdateReqDto memberUpdateReqDto,
                                     MultipartFile image, CustomUserDetails userDetails);

    MemberSaveRespDto deleteMember(CustomUserDetails userDetails);

    MemberSelectRespDto getMemberByMemberId(CustomUserDetails userDetails);

    MemberSMSRespDto sendSMS(MemberSMSReqDto memberSMSReqDto);
}