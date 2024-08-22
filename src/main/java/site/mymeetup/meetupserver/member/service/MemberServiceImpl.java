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
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.interest.repository.InterestSmallRepository;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.entity.MemberInterest;
import site.mymeetup.meetupserver.member.repository.MemberInterestRepository;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static site.mymeetup.meetupserver.member.dto.MemberDto.*;
import static site.mymeetup.meetupserver.member.dto.MemberInterestDto.MemberInterestSaveRespDto;

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
    private final InterestSmallRepository interestSmallRepository;
    private final MemberInterestRepository memberInterestRepository;


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

        AES128 aes = new AES128("AES_KEY");

        // 패스워드, 핸드폰 인코딩
        memberUpdateReqDto.encodeFields(passwordEncoder, aes);

        Member member = validateMember(userDetails.getMemberId());


        // 비활성화 회원인지 검증
        int status = userDetails.getStatus();
        if (status == 2) {
            throw new CustomException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        // 관심지역 검증
        Geo geo = validateGeo(memberUpdateReqDto.getGeoId());

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

        member.updateMember(memberUpdateReqDto.toEntity(geo, originalImg, saveImg));

        Member updatedMember = memberRepository.save(member);

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

    @Override
    public List<MemberInterestSaveRespDto> createMemberInterests(Long memberId, List<Long> interestSmallIds, CustomUserDetails userDetails) {
        // 로그인한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 로그인한 사용자와 요청한 회원 ID가 일치하는지 검증
        if (!memberId.equals(userDetails.getMemberId())) {
            throw new CustomException(ErrorCode.MEMBER_INVALID_INTEREST); // 권한 없음 예외
        }

        // 특정 회원의 모든 관심사 조회
        Set<Long> existingInterestIds = memberInterestRepository.findMemberInterestByMember_MemberId(memberId).stream()
                .map(mi -> mi.getInterestSmall().getInterestSmallId())
                .collect(Collectors.toSet());

        // 관심사는 최대 5개로 제한
        if (existingInterestIds.size() + interestSmallIds.size() > 5) {
            throw new CustomException(ErrorCode.MEMBER_INVALID_INTEREST); // 최대 개수 초과
        }

        List<MemberInterestSaveRespDto> savedInterests = new ArrayList<>();

        for (Long interestSmallId : interestSmallIds) {
            // 관심사 소분류 검증 및 중복 체크
            InterestSmall interestSmall = validateInterestSmall(interestSmallId);
            // 중복된 관심사 체크 (현재 회원의 관심사와 비교)
            if (existingInterestIds.contains(interestSmallId)) {
                throw new CustomException(ErrorCode.MEMBER_INVALID_INTEREST); // 중복된 관심사
            }

            // 회원 관심사 생성 및 저장
            MemberInterest savedMemberInterest = memberInterestRepository.save(
                    MemberInterest.builder()
                            .member(member)
                            .interestSmall(interestSmall)
                            .build()
            );

            savedInterests.add(new MemberInterestSaveRespDto(savedMemberInterest));
        }

        return savedInterests;
    }

    @Override
    public List<MemberInterestSaveRespDto> updateMemberInterests(Long memberId, List<Long> interestSmallIds, CustomUserDetails userDetails) {
        // 로그인한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 로그인한 사용자와 요청한 회원 ID가 일치하는지 검증
        if (!memberId.equals(userDetails.getMemberId())) {
            throw new CustomException(ErrorCode.MEMBER_INVALID_INTEREST); // 권한 없음 예외
        }

        // 기존 관심사 조회
        List<MemberInterest> existingInterests = memberInterestRepository.findMemberInterestByMember_MemberId(memberId);

        // 새로운 관심사 목록을 저장할 리스트
        List<MemberInterestSaveRespDto> updatedInterests = new ArrayList<>();

        for (Long interestSmallId : interestSmallIds) {
            // 관심사 소분류 검증
            InterestSmall interestSmall = validateInterestSmall(interestSmallId);
            boolean found = false;

            // 기존 관심사에서 수정 또는 추가
            for (MemberInterest existingInterest : existingInterests) {
                if (existingInterest.getInterestSmall().getInterestSmallId().equals(interestSmallId)) {
                    // 기존 관심사 수정
                    MemberInterest updatedInterest = MemberInterest.builder()
                            .memberInterestId(existingInterest.getMemberInterestId()) // 기존 ID 유지
                            .member(existingInterest.getMember()) // 기존 회원 유지
                            .interestSmall(interestSmall) // 새로운 관심사 소분류 설정
                            .build();
                    // 수정된 관심사 저장
                    memberInterestRepository.save(updatedInterest);
                    updatedInterests.add(new MemberInterestSaveRespDto(updatedInterest));
                    found = true;
                    break; // 수정 후 반복 종료
                }
            }

            // 기존 관심사가 없는 경우 새로 추가
            if (!found) {
                MemberInterest newMemberInterest = memberInterestRepository.save(
                        MemberInterest.builder()
                                .member(member)
                                .interestSmall(interestSmall)
                                .build()
                );
                updatedInterests.add(new MemberInterestSaveRespDto(newMemberInterest));
            }
        }

        log.info("회원 관심사가 업데이트되었습니다."); // 로그 기록
        return updatedInterests; // 수정된 관심사 리스트 반환
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

    // 상세 관심사 검증 후 INTEREST_SMALL 엔티티 반환
    private InterestSmall validateInterestSmall(Long interestSmallId) {
        if (interestSmallId == null) {
            return null;
        }
        return interestSmallRepository.findById(interestSmallId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_SMALL_NOT_FOUND));
    }
}