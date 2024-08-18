package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.MessageService;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.dto.MemberDto;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import java.io.IOException;

import static site.mymeetup.meetupserver.member.dto.MemberDto.*;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final GeoRepository geoRepository;
    private final S3ImageService s3ImageService;
    private final MessageService messageService;

    // 회원 가입
    @Override
    public MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto) {

        // 핸드폰으로 신규 회원인지 검증
        Member memberExists = memberRepository.findByPhone(memberSaveReqDto.getPhone());
        if (memberExists != null) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(memberSaveReqDto.getGeoId());

        // 비밀번호 인코딩
        memberSaveReqDto.encodePassword(new BCryptPasswordEncoder());

        // DTO -> Entity 변환 및 저장
        Member member = memberSaveReqDto.toEntity(geo);
        memberRepository.save(member);

        return MemberSaveRespDto.builder().member(member).build();
    }

    @Override
    public Member createSNSMember(MemberDto.MemberSNSReqDto memberSNSReqDto) {
        // 핸드폰으로 신규 회원인지 검증
        Member memberExists = memberRepository.findByPhone(memberSNSReqDto.getPhone());
        if (memberExists != null) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(memberSNSReqDto.getGeoId());

        // DTO -> Entity 변환 및 저장
        Member newMember = memberSNSReqDto.toEntity(geo);
        memberRepository.save(newMember);

        return newMember;
    }


    // 로그인 사용자 정보 조회
    @Override
    public MemberInfoDto getMemberInfo() {
        // 현재 로그인된 사용자 정보를 SecurityContextHolder에서 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new CustomException(ErrorCode.MEMBER_UNAUTHORIZED);
        }

        // 로그인한 사용자의 ID 가져오기
        Long loginMemberId = userDetails.getMemberId();

        // 로그인한 사용자의 정보 검증
        Member member = validateMember(loginMemberId);

        return MemberInfoDto.builder()
                .member(member)
                .build();
    }

    @Override
    public MemberUpdateRespDto updateMember(Long memberId, MemberUpdateReqDto memberUpdateReqDto, MultipartFile image, CustomUserDetails userDetails) {
        // 로그인한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(memberUpdateReqDto.getGeoId());

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

        // 로그인한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 회원 상태값 변경
        member.changeMemberStatus(0);
        // DB 수정
        memberRepository.save(member);
        return new MemberSaveRespDto(member);
    }

    // 특정 회원 조회
    @Override
    public MemberSelectRespDto getMemberByMemberId(Long memberId, CustomUserDetails userDetails) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberSelectRespDto.builder().member(member).build();
    }

    @Override
    public MemberSMSRespDto sendSMS(MemberSMSReqDto memberSMSReqDto) {
        Member member = memberRepository.findByPhone(memberSMSReqDto.getPhone());

        // 이미 존재하는 회원일 시 에러 반환
        if (member != null) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        int randomNum = (int)(Math.random()* (9999 - 1000 +1)) + 1000;

        Message message = new Message();
        message.setFrom("01065639503");
        message.setTo(memberSMSReqDto.getPhone());
        message.setText("[MEETUP] 인증번호는" + "[" + randomNum + "]" + "입니다.");

        // 메시지 전송
        messageService.sendOne(message);

        return MemberSMSRespDto.builder().randomNum(randomNum).build();
    }

    // 지역 검증 후 GEO 엔티티 반환
    private Geo validateGeo(Long geoId) {
        return geoRepository.findById(geoId)
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));
    }

    // 사용자 검증 후 MEMBER 엔티티 반환
    private Member validateMember(Long memberId) {
        return memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}