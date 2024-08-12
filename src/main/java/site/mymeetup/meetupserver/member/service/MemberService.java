package site.mymeetup.meetupserver.member.service;


import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.MemberDto;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.UserInfoDto;

public interface MemberService {
    MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto);

    MemberSaveRespDto updateMember(Long memberId, MemberSaveReqDto memberSaveReqDto,
                                   MultipartFile image);

    MemberSelectRespDto getMemberByMemberId(Long memberId);

    MemberSaveRespDto deleteMember(Long memberId);

    UserInfoDto getUserInfoByMemberId(Long memberId);
}