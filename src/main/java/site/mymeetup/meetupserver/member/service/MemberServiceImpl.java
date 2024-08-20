package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.common.service.MessageService;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.config.AES128;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import static site.mymeetup.meetupserver.member.dto.MemberDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final GeoRepository geoRepository;
    private final S3ImageService s3ImageService;
    private final MessageService messageService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AES128 aes128;


    // 회원 가입
    @Override
    public MemberSaveRespDto createMember(MemberSaveReqDto memberSaveReqDto) {
        AES128 aes = new AES128("AES_KEY");

        memberSaveReqDto.encodeFields(passwordEncoder, aes);

        // 핸드폰으로 신규 회원인지 검증
        Member member = memberRepository.findByPhone(memberSaveReqDto.getPhone());
        if (member != null) {
            if (member.getStatus() == 1 || member.getStatus() == 2)
                throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(memberSaveReqDto.getGeoId());

        // DTO -> Entity 변환 및 저장
        Member newMember = memberSaveReqDto.toEntity(geo);
        memberRepository.save(newMember);

        return MemberSaveRespDto.builder().member(newMember).build();
    }

    // 로그인 사용자 정보 조회
    @Override
    public MemberInfoDto getMemberInfo(CustomUserDetails userDetails) {

        // 로그인한 사용자의 ID 가져오기
        Long loginMemberId = userDetails.getMemberId();

        // 로그인한 사용자의 정보 검증
        Member member = validateMember(loginMemberId);

        return MemberInfoDto.builder().member(member).build();
    }

    @Override
    public MemberUpdateRespDto updateMember(Long memberId, MemberUpdateReqDto memberUpdateReqDto,
                                            MultipartFile image, CustomUserDetails userDetails) {

        // Create an instance of AES128
        AES128 aes = new AES128("AES_KEY");

        // Log the incoming request details
        log.info("Update request for member ID: {}", memberId);
        log.info("Request DTO: {}", memberUpdateReqDto);

        // Encode fields in DTO
        memberUpdateReqDto.encodeFields(passwordEncoder, aes);
        log.debug("Encoded password: {}", memberUpdateReqDto.getPassword());
        log.debug("Encoded phone: {}", memberUpdateReqDto.getPhone());

        // Validate logged-in user
        Member member = validateMember(userDetails.getMemberId());
        log.info("Member found for update: {}", member);

        // Check if the user is active
        int status = userDetails.getStatus();
        if (status == 2) {
            log.error("Inactive member status: {}", status);
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // Validate Geo entity
        Geo geo = validateGeo(memberUpdateReqDto.getGeoId());
        log.info("Geo validated: {}", geo);

        // Handle image upload
        String originalImg = null;
        String saveImg = null;

        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
            log.info("Image uploaded: {}", originalImg);
        } else if (memberUpdateReqDto.getOriginalImg() != null && memberUpdateReqDto.getSaveImg() != null) {
            if (!memberUpdateReqDto.getSaveImg().equals(member.getSaveImg())
                    && !memberUpdateReqDto.getOriginalImg().equals(member.getOriginalImg())) {
                log.error("Image mismatch: DTO saveImg={} but DB saveImg={}", memberUpdateReqDto.getSaveImg(), member.getSaveImg());
                throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
            }
            originalImg = memberUpdateReqDto.getOriginalImg();
            saveImg = memberUpdateReqDto.getSaveImg();
            log.info("Using existing images: originalImg={}, saveImg={}", originalImg, saveImg);
        } else if (memberUpdateReqDto.getOriginalImg() != null || memberUpdateReqDto.getSaveImg() != null) {
            log.error("One of the image fields is set but not both.");
            throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
        }

        // Update the member entity with new data
        member.updateMember(memberUpdateReqDto.toEntity(geo, originalImg, saveImg));
        log.info("Member updated entity: {}", member);

        // Save the updated member entity
        Member updatedMember = memberRepository.save(member);
        log.info("Member saved to DB: {}", updatedMember);

        return MemberUpdateRespDto.builder().member(updatedMember).build();
    }



    //회원 삭제
    @Override
    public void deleteMember(Long memberId, CustomUserDetails userDetails) {

        // 로그인한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 회원 상태값 변경
        member.changeMemberStatus(0);
        // DB 수정
        memberRepository.save(member);
    }

    // 특정 회원 조회
    @Override
    public MemberSelectRespDto getMemberByMemberId(Long memberId) {

        // 로그인한 사용자의 정보 검증
        Member member = validateMember(memberId);

        return MemberSelectRespDto.builder().member(member).build();
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

    @Override
    public MemberSMSRespDto sendSMS(MemberSMSReqDto memberSMSReqDto) {
        int randomNum = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;

        Message message = new Message();
        message.setFrom("01065639503");
        message.setTo(memberSMSReqDto.getPhone());
        message.setText("[MEETUP] 인증번호는" + "[" + randomNum + "]" + "입니다.");

        // 메시지 전송
        messageService.sendOne(message);

        return MemberSMSRespDto.builder().randomNum(randomNum).build();
    }
}