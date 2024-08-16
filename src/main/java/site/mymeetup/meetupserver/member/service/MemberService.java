package site.mymeetup.meetupserver.member.service;


import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.dto.MemberDto;


import java.io.IOException;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateRespDto;

public interface MemberService {
    MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto);

    MemberInfoDto getUserInfoByMemberId(Long memberId, CustomUserDetails userDetails);

    MemberUpdateRespDto updateMember(Long memberId, MemberDto.MemberUpdateReqDto memberUpdateReqDto,
                                     MultipartFile image, CustomUserDetails userDetails);

    MemberSaveRespDto deleteMember(Long memberId, CustomUserDetails userDetails);

    MemberSelectRespDto getMemberByMemberId(Long memberId, CustomUserDetails userDetails);

    String getAccessTokenFromProvider(String provider, String code) throws IOException;

    CustomUserDetails getUserInfoFromProvider(String provider, String accessToken) throws IOException;

    String generateJwtToken(CustomUserDetails userDetails);
}