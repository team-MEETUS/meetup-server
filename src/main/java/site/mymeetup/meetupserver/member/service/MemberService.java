package site.mymeetup.meetupserver.member.service;

import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.MemberDto;

public interface MemberService {
    MemberDto.MemberSaveRespDto createMember(MemberDto.MemberSaveReqDto memberSaveReqDto, MultipartFile image);

    MemberDto.MemberSaveRespDto updateMember(Long memberId,
                                             MemberDto.MemberSaveReqDto memberSaveReqDto, MultipartFile image);

    void deleteMember(Long memberId);

    MemberDto.MemberSelectRespDto getMemberByMemberId(Long memberId);
}