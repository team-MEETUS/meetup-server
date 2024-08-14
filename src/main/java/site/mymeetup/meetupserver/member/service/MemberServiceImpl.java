package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSelectRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateReqDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberUpdateRespDto;
import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberInfoDto;

import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final GeoRepository geoRepository;
    private final S3ImageService s3ImageService;

    // 회원 가입
    @Override
    public MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto) {
        // geoId로 Geo 객체 조회
        Geo geo = geoRepository.findById(memberSaveReqDto.getGeoId())
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));

        Member member = memberRepository.save(memberSaveReqDto.toEntity(geo));
        return MemberSaveRespDto.builder().member(member).build();
    }

    // 로그인 사용자 정보 조회
    @Override
    public MemberInfoDto getUserInfoByMemberId(Long memberId, CustomUserDetails userDetails) {
        // 핸드폰 번호와 상태값으로 해당 회원이 존재하는지 검증
        Member member = memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 로그인한 사용자와 요청된 사용자가 일치하지 않으면 예외 처리
        Long loginMemberId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMemberId();
        if (!loginMemberId.equals(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_UNAUTHORIZED);
        }

        return MemberInfoDto.builder().member(member).build();
    }

    @Override
    public MemberUpdateRespDto updateMember(Long memberId, MemberUpdateReqDto memberUpdateReqDto, MultipartFile image, CustomUserDetails userDetails) {
        // 핸드폰 번호로 해당 회원이 존재하는지 확인
        Member member = memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 지역이 존재하는지 확인
        Geo geo = geoRepository.findById(memberUpdateReqDto.getGeoId())
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));

        // S3 이미지 업로드
        String originalImg = null;
        String saveImg = null;

        // 이미지 변경하는 경우 업로드 후 DB저장
        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
        }
        // 이미지를 변경하지 않는 경우 기존 이미지 그대로
        else if (memberUpdateReqDto.getOriginalImg() != null && memberUpdateReqDto.getSaveImg() != null) {
            if (!memberUpdateReqDto.getSaveImg().equals(member.getSaveImg())
                    && !memberUpdateReqDto.getOriginalImg().equals(member.getOriginalImg())) {
                throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
            }
            originalImg = memberUpdateReqDto.getOriginalImg();
            saveImg = memberUpdateReqDto.getSaveImg();
        }
        //원본/저장 둘 중 하나만 널일 경우 삭제
        else if (memberUpdateReqDto.getOriginalImg() != null || memberUpdateReqDto.getSaveImg() != null) {
            throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
        }

        // dto -> entity
        member.updateMember(memberUpdateReqDto.toEntity(geo, originalImg, saveImg));
        // DB 수정
        Member updatedMember = memberRepository.save(member);

        return MemberUpdateRespDto.builder().member(updatedMember).build();
    }

    //회원 삭제
    @Override
    public MemberSaveRespDto deleteMember(Long memberId, CustomUserDetails userDetails) {
        // 핸드폰 번호로 해당 회원이 존재하는지 검증
        Member member = memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        // 회원 상태값 변경
        member.changeMemberStatus(0);
        // DB 수정
        memberRepository.save(member);
        return null;
    }

    // 특정 회원 조회
    @Override
    public MemberSelectRespDto getMemberByMemberId(Long memberId, CustomUserDetails userDetails) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberSelectRespDto.builder().member(member).build();
    }

}