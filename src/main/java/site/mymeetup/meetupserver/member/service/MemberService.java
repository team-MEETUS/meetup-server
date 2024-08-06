package site.mymeetup.meetupserver.member.service;

import site.mymeetup.meetupserver.member.dto.MemberDto;

public interface MemberService {
    MemberDto.MemberSaveRespDto createMember(MemberDto.MemberSaveReqDto memberSaveReqDto);
    MemberDto.MemberSelectRespDto getMemberByMemberId(Long memberId);
}