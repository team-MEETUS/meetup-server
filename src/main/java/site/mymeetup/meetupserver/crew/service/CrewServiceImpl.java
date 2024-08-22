package site.mymeetup.meetupserver.crew.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.mymeetup.meetupserver.chat.entity.Chat;
import site.mymeetup.meetupserver.chat.repository.ChatRepository;
import site.mymeetup.meetupserver.common.service.S3ImageService;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewLike;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewLikeRepository;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.interest.entity.InterestBig;
import site.mymeetup.meetupserver.interest.entity.InterestSmall;
import site.mymeetup.meetupserver.interest.repository.InterestBigRepository;
import site.mymeetup.meetupserver.interest.repository.InterestSmallRepository;
import site.mymeetup.meetupserver.meeting.service.MeetingService;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;
import site.mymeetup.meetupserver.notifacation.repository.NotificationRepository;
import site.mymeetup.meetupserver.notifacation.service.NotificationService;

import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewSelectRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewDetailRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewInterestReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewDto.CrewChatRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveReqDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSaveRespDto;
import static site.mymeetup.meetupserver.crew.dto.CrewMemberDto.CrewMemberSelectRespDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrewServiceImpl implements CrewService {
    private final CrewRepository crewRepository;
    private final GeoRepository geoRepository;
    private final InterestBigRepository interestBigRepository;
    private final InterestSmallRepository interestSmallRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewLikeRepository crewLikeRepository;
    private final ChatRepository chatRepository;
    private final S3ImageService s3ImageService;
    private final NotificationService notificationService;
    private final MeetingService meetingService;

    // 모임 등록
    public CrewSaveRespDto createCrew(CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(crewSaveReqDto.getGeoId());

        // 관심사 대분류가 존재하는지 확인
        InterestBig interestBig = validateInterestBig(crewSaveReqDto.getInterestBigId());

        // 관심사 소분류가 존재하는지 확인
        InterestSmall interestSmall = validateInterestSmall(crewSaveReqDto.getInterestSmallId());

        // interestSmall의 interestBig 값이 interestBig와 같은지 확인
        if (interestSmall != null) {
            if (!interestSmall.getInterestBig().getInterestBigId().equals(interestBig.getInterestBigId())) {
                throw new CustomException(ErrorCode.INTEREST_BAD_REQUEST);
            }
        }

        // S3 이미지 업로드
        String originalImg = null;
        String saveImg = null;
        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
        }

        // dto -> entity
        Crew crew = crewRepository.save(crewSaveReqDto.toEntity(geo, interestBig, interestSmall, originalImg, saveImg));

        // 모임 멤버 등록
        CrewMember crewMember = CrewMember.builder()
                .role(CrewMemberRole.LEADER)
                .crew(crew)
                .member(member)
                .build();
        crewMemberRepository.save(crewMember);

        return CrewSaveRespDto.builder().crew(crew).build();
    }

    // 모임 수정
    public CrewSaveRespDto updateCrew(Long crewId, CrewSaveReqDto crewSaveReqDto, MultipartFile image, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        // 해당 모임의 모임장인지 확인
        if (!crewMemberRepository.existsByCrewAndMemberAndRole(crew, member, CrewMemberRole.LEADER)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        }

        // 지역이 존재하는지 확인
        Geo geo = validateGeo(crewSaveReqDto.getGeoId());

        // 관심사 대분류가 존재하는지 확인
        InterestBig interestBig = validateInterestBig(crewSaveReqDto.getInterestBigId());

        // 관심사 소분류가 존재하는지 확인
        InterestSmall interestSmall = validateInterestSmall(crewSaveReqDto.getInterestSmallId());

        // interestSmall의 interestBig 값이 interestBig와 같은지 확인
        if (interestSmall != null) {
            if (!interestSmall.getInterestBig().getInterestBigId().equals(interestBig.getInterestBigId())) {
                throw new CustomException(ErrorCode.INTEREST_BAD_REQUEST);
            }
        }

        // S3 이미지 업로드
        String originalImg = null;
        String saveImg = null;

        // 이미지 변경 O => 변경하는 이미지 업로드 후 DB 저장
        if (!image.isEmpty()) {
            saveImg = s3ImageService.upload(image);
            originalImg = image.getOriginalFilename();
        }
        // 이미지 변경 X => 기존 이미지 그대로 가져감
        else if (crewSaveReqDto.getOriginalImg() != null && crewSaveReqDto.getSaveImg() != null) {
            if (!crewSaveReqDto.getSaveImg().equals(crew.getSaveImg()) && !crewSaveReqDto.getOriginalImg().equals(crew.getOriginalImg())) {
                throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
            }
            originalImg = crewSaveReqDto.getOriginalImg();
            saveImg = crewSaveReqDto.getSaveImg();
        }
        // 원본 이미지 또는 저장 이미지 하나만 널일 경우
        else if (crewSaveReqDto.getOriginalImg() != null || crewSaveReqDto.getSaveImg() != null) {
            throw new CustomException(ErrorCode.IMAGE_BAD_REQUEST);
        }
        // 이미지 삭제 => null

        // Crew 객체 업데이트
        crew.updateCrew(crewSaveReqDto.toEntity(geo, interestBig, interestSmall, originalImg, saveImg));

        // DB 수정
        Crew updateCrew = crewRepository.save(crew);

        return CrewSaveRespDto.builder().crew(updateCrew).build();
    }

    // 모임 삭제
    public void deleteCrew(Long crewId, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        // 해당 모임의 모임장인지 확인
        if (!crewMemberRepository.existsByCrewAndMemberAndRole(crew, member, CrewMemberRole.LEADER)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        }

        // 삭제할 모임 상태값 변경
        crew.changeStatus(0);

        // DB 수정
        crewRepository.save(crew);
    }

    // 모임 상세 조회
    public CrewDetailRespDto getCrewByCrewId(Long crewId) {
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        return CrewDetailRespDto.builder().crew(crew).build();
    }

    // 관심사 별 모임 조회
    @Override
    public List<CrewSelectRespDto> getAllCrewByInterest(CrewInterestReqDto crewInterestReqDto, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        String city = null;
        if (userDetails != null) {
            Member member = validateMember(userDetails.getMemberId());
            city = member.getGeo().getCity();
        }

        // 관심사 대분류 검증
        InterestBig interestBig = null;
        InterestSmall interestSmall = null;
        if (crewInterestReqDto.getInterestBigId() != null) {
            interestBig = validateInterestBig(crewInterestReqDto.getInterestBigId());
        }
        if (crewInterestReqDto.getInterestSmallId() != null) {
            interestSmall = validateInterestSmall(crewInterestReqDto.getInterestSmallId());
        }

        // 페이지 번호 유효성 검사
        if (crewInterestReqDto.getPage() < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }
        int page = crewInterestReqDto.getPage() - 1;

        // 모임 리스트 조회
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "totalMember"));
        Page<Crew> crews = crewRepository.findCrewsByInterest(city, interestBig, interestSmall, pageable);

        return crews.stream()
                .map(CrewSelectRespDto::new)
                .toList();
    }

    @Override
    public List<CrewChatRespDto> getActiveCrew(int page, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        String city = null;
        if (userDetails != null) {
            Member member = validateMember(userDetails.getMemberId());
            city = member.getGeo().getCity();
        }

        // 페이지 번호 유효성 검사
        if (page < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        return getCrewWithLastChatTime(city, page);
    }

    @Override
    public List<CrewSelectRespDto> getNewCrew(int page, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        String city = null;
        if (userDetails != null) {
            Member member = validateMember(userDetails.getMemberId());
            city = member.getGeo().getCity();
        }

        // 페이지 번호 유효성 검사
        if (page < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Crew> crews = crewRepository.newCrews(city, pageable);

        return crews.stream()
                .map(CrewSelectRespDto::new)
                .toList();
    }

    @Override
    public List<CrewSelectRespDto> getMyCrew(CustomUserDetails userDetails) {
        // 현재 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임의 멤버인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        // 사용자가 속한 모임 조회
        List<Crew> crews = crewMemberRepository.findCrewsByMember(member, roles);

        return crews.stream()
                .map(CrewSelectRespDto::new)
                .toList();
    }

    // 모임 검색
    @Override
    public List<CrewSelectRespDto> getSearchCrew(String keyword, int page, CustomUserDetails userDetails) {
        // 로그인 한 사용자 검증
        String city = null;
        if (userDetails != null) {
            Member member = validateMember(userDetails.getMemberId());
            city = member.getGeo().getCity();
        }

        // 페이지 번호 유효성 검사
        if (page < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_NUMBER);
        }

        Pageable pageable = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "totalMember"));
        Page<Crew> crews = crewRepository.searchCrews(keyword, city, pageable);

        return crews.stream()
                .map(CrewSelectRespDto::new)
                .toList();
    }

    // 로그인 한 사용자의 권한 조회
    @Override
    public CrewMemberRole getCrewMemberRole(Long crewId, CustomUserDetails userDetails) {
        // 현재 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        // 해당 모임의 멤버인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        CrewMember crewMember = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElse(null);

        return crewMember != null ? crewMember.getRole() : null;
    }

    // 모임 가입 신청
    @Override
    public CrewMemberSaveRespDto signUpCrew(Long crewId, CustomUserDetails userDetails) {
        // 현재 로그인 한 사용자 정보 가져오기
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임이 존재 하는지 검증
        Crew crew = validateCrew(crewId);

        // 이미 모임원 이거나 강퇴 , 승인 대기 중인지 확인
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.EXPELLED,
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER,
                CrewMemberRole.PENDING
        );

        CrewMember crewMember = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles).orElse(null);
        if (crewMember != null) {
            if (crewMember.getRole() == CrewMemberRole.MEMBER || crewMember.getRole() == CrewMemberRole.ADMIN || crewMember.getRole() == CrewMemberRole.LEADER) {
                throw new CustomException(ErrorCode.ALREADY_CREW_MEMBER);
            } else if (crewMember.getRole() == CrewMemberRole.EXPELLED) {
                throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
            } else if (crewMember.getRole() == CrewMemberRole.PENDING) {
                throw new CustomException(ErrorCode.ALREADY_PENDING);
            }
        }

        // 알림 전송
        notificationService.notifyPending(crewId);

        // 모임멤버 추가
        CrewMember saveCrewMember = CrewMember.builder()
                .role(CrewMemberRole.PENDING)
                .crew(crew)
                .member(member)
                .build();

        return CrewMemberSaveRespDto.builder().crewMember(crewMemberRepository.save(saveCrewMember)).build();
    }

    // 특정 모임의 모임원 조회
    public List<CrewMemberSelectRespDto> getCrewMemberByCrewId(Long crewId) {
        // 유효한 모임인지 검증
        Crew crew = validateCrew(crewId);

        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        List<CrewMember> crewMembers = crewMemberRepository.findByCrewAndRoleInOrderByRoleDesc(crew, roles);

        return crewMembers.stream()
                .map(CrewMemberSelectRespDto::new)
                .toList();
    }

    // 특정 모임의 가입신청 조회
    public List<CrewMemberSelectRespDto> getSignUpMemberByCrewId(Long crewId, CustomUserDetails userDetails) {
        // 현재 로그인 한 사용자 정보 가져오기
        Member member = validateMember(userDetails.getMemberId());

        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        // 해당 모임의 멤버인지 확인
        if (!crewMemberRepository.existsByCrewAndMemberAndRole(crew, member, CrewMemberRole.LEADER)
            && !crewMemberRepository.existsByCrewAndMemberAndRole(crew, member, CrewMemberRole.ADMIN)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        }

        List<CrewMember> crewMembers = crewMemberRepository.findByCrewAndRole(crew, CrewMemberRole.PENDING);

        return crewMembers.stream()
                .map(CrewMemberSelectRespDto::new)
                .toList();
    }

    // 권한 변경
    public CrewMemberSaveRespDto updateRole(Long crewId, CrewMemberSaveReqDto crewMemberSaveReqDto, CustomUserDetails userDetails) {
        // 해당 모임이 존재하는지 검증
        Crew crew = validateCrew(crewId);

        // 현재 로그인 한 사용자(변경자) 정보 가져오기
        Member initiatorMember = validateMember(userDetails.getMemberId());

        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER,
                CrewMemberRole.MEMBER
        );
        CrewMember initiator = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, initiatorMember, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_ACCESS_DENIED));

        // 변경 대상자 정보 가져오기
        Member targetMember = validateMember(crewMemberSaveReqDto.getMemberId());

        roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER,
                CrewMemberRole.PENDING
        );
        CrewMember target = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, targetMember, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        // 변경할 역할
        CrewMemberRole newRole;
        try {
            newRole = CrewMemberRole.valueOf(crewMemberSaveReqDto.getNewRoleStatus());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_ROLE);
        }

        // role 변경 가능한지 확인
        canChangeRole(initiator.getRole(), target.getRole(), newRole);

        // 일반멤버는 탈퇴만 가능
        if (initiator.getRole() == CrewMemberRole.MEMBER) {
            if (initiator.equals(target) || newRole != CrewMemberRole.DEPARTED) {
                throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
            }
        }

        // 본인의 상태는 퇴장으로만 변경 가능
        if (initiator == target && newRole != CrewMemberRole.DEPARTED) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        }

        // 모임장을 임명하는 경우 , 로그인 유저는 운영진으로 변경
        if (newRole == CrewMemberRole.LEADER) {
            initiator.updateRole(CrewMemberRole.ADMIN);
            crewMemberRepository.save(initiator);
        }
        // 가입 신청 승인시 총 모임원 수 +1
        if (target.getRole() == CrewMemberRole.PENDING && newRole == CrewMemberRole.MEMBER) {
            // 모임의 정원이 다 찼는지 확인
            if (crew.getMax() == crew.getTotalMember()) {
                throw new CustomException(ErrorCode.MAX_CREW_MEMBER);
            }
            crew.changeTotalMember(1);
            crewRepository.save(crew);

            // 알림 전송
            notificationService.notifyApproval(crewId, targetMember.getMemberId());
        }
        // 회원 강퇴 또는 퇴장 시 총 모임원 수 -1 & 정모에서 삭제
        if ((target.getRole() == CrewMemberRole.MEMBER && newRole == CrewMemberRole.EXPELLED) || (newRole == CrewMemberRole.DEPARTED)) {
            crew.changeTotalMember(-1);
            crewRepository.save(crew);
            meetingService.deleteMeetingMember(crew, targetMember);
        }

        // 권한 변경
        target.updateRole(newRole);
        CrewMember crewMember = crewMemberRepository.save(target);

        return CrewMemberSaveRespDto.builder().crewMember(crewMember).build();
    }

    // 모임 좋아요
    public void likeCrew(Long crewId, CustomUserDetails userDetails) {
        // 현재 로그인 한 사용자 검증
        Member member = validateMember(userDetails.getMemberId());

        // 모임 검증
        Crew crew = validateCrew(crewId);

        // 좋아요를 했는지 확인
        CrewLike crewLike = crewLikeRepository.findByCrewAndMember(crew, member);

        if (crewLike == null) {
            // 총 좋아요 수 +1
            crew.changeTotalLike(1);
            crewRepository.save(crew);

            // 좋아요 등록
            crewLike = CrewLike.builder()
                               .crew(crew)
                               .member(member)
                               .build();
            crewLikeRepository.save(crewLike);
        } else {
            // 총 좋아요 수 -1
            crew.changeTotalLike(-1);
            crewRepository.save(crew);

            // 좋아요 취소
            crewLikeRepository.delete(crewLike);
        }
    }

    // 모임 찜 여부 조회
    public boolean isLikeCrew(Long crewId, CustomUserDetails userDetails) {
        // 현재 로그인 한 유저 검증
        Member member = validateMember(userDetails.getMemberId());

        // 모임 검증
        Crew crew = validateCrew(crewId);

        return crewLikeRepository.existsByCrewAndMember(crew, member);
    }

    // 마지막 채팅 시간 조회 및 페이징 처리
    private List<CrewChatRespDto> getCrewWithLastChatTime(String city, int page) {
        // 전체 모임 조회
        List<Crew> crews = crewRepository.activeCrew(city);

        // 마지막 채팅 시간 매핑
        List<CrewChatRespDto> crewList = new ArrayList<>();
        for (Crew crew : crews) {
            LocalDateTime lastChatTime = getLastChatTime(crew.getCrewId());
            if (lastChatTime != null) {
                crewList.add(new CrewChatRespDto(crew, lastChatTime));
            }
        }

        // 마지막 대화 시간 기준 정렬
        crewList.sort(Comparator.comparing(CrewChatRespDto::getLastChatTime).reversed());

        // 페이징 처리
        int size = 20;
        int start = (page - 1) * size;
        if (start >= crewList.size()) {
            return new ArrayList<>();
        }
        int end = Math.min(start + size, crewList.size());
        return crewList.subList(start, end);
    }

    // MongoDB 에서 마지막 채팅 시간 조회
    private LocalDateTime getLastChatTime(Long crewId) {
        return chatRepository.findFirstByCrewIdAndReceiverIdIsNullOrderByCreateDateDesc(crewId)
                .map(Chat::getCreateDate)
                .block();
    }

    // 사용자 검증 후 MEMBER 엔티티 반환
    private Member validateMember(Long memberId) {
        return memberRepository.findByMemberIdAndStatus(memberId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 모임 검증 후 CREW 엔티티 반환
    private Crew validateCrew(Long crewId) {
        return crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
    }

    // 지역 검증 후 GEO 엔티티 반환
    private Geo validateGeo(Long geoId) {
        return geoRepository.findById(geoId)
                .orElseThrow(() -> new CustomException(ErrorCode.GEO_NOT_FOUND));
    }

    // 관심사 검증 후 INTEREST_BIG 엔티티 반환
    private InterestBig validateInterestBig(Long interestBigId) {
        return interestBigRepository.findById(interestBigId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_BIG_NOT_FOUND));
    }

    // 상세 관심사 검증 후 INTEREST_SMALL 엔티티 반환
    private InterestSmall validateInterestSmall(Long interestSmallId) {
        if (interestSmallId == null) {
            return null;
        }
        return interestSmallRepository.findById(interestSmallId)
                .orElseThrow(() -> new CustomException(ErrorCode.INTEREST_SMALL_NOT_FOUND));
    }

    // role 변경 권한 검사
    private void canChangeRole(CrewMemberRole initiatorRole, CrewMemberRole targetRole, CrewMemberRole newRole) {
        if (targetRole == CrewMemberRole.LEADER) {
            throw new CustomException(ErrorCode.LEADER_PERMISSION_DENIED);
        } else if (initiatorRole == CrewMemberRole.ADMIN && newRole == CrewMemberRole.LEADER) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        } else if (targetRole == CrewMemberRole.MEMBER && !targetRole.canMemberChangeTo(newRole)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        } else if (targetRole == CrewMemberRole.ADMIN && !targetRole.canAdminChangeTo(newRole)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        } else if (targetRole == CrewMemberRole.PENDING && !targetRole.canPendingChangeTo(newRole)) {
            throw new CustomException(ErrorCode.CREW_ACCESS_DENIED);
        }
    }

}
