package site.mymeetup.meetupserver.notifacation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.board.repository.BoardRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;
import site.mymeetup.meetupserver.notifacation.entity.Notification;
import site.mymeetup.meetupserver.notifacation.repository.NotificationRepository;
import site.mymeetup.meetupserver.notifacation.type.NotificationType;
import static site.mymeetup.meetupserver.notifacation.dto.NotificationDto.NotificationRespDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();
    private final Map<Long, Integer> notificationCounts = new HashMap<>();
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final BoardRepository boardRepository;

    // SSE 연결
    @Override
    public SseEmitter createEmitter(CustomUserDetails userDetails) {
        Long receiverId = userDetails.getMemberId();

        // sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃 설정

        // 연결
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));

            // 초기 알림 개수 조회 및 map 에 저장
            int initialNotificationCount = notificationRepository.countByMember_MemberIdAndIsRead(receiverId, false);
            notificationCounts.put(receiverId, initialNotificationCount);

            // 알림 개수 전송
            Map<String, Integer> countData = new HashMap<>();
            countData.put("notificationCount", notificationCounts.get(receiverId));

            sseEmitter.send(SseEmitter.event().name("notification").data(countData));
        } catch (IOException e) {
            log.error("Error while sending SSE connection event for memberId {}: {}", receiverId, e.getMessage(), e);
        }

        // 저장
        sseEmitters.put(receiverId, sseEmitter);

        // 연결 종료
        sseEmitter.onCompletion(() -> sseEmitters.remove(receiverId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(receiverId));
        sseEmitter.onError((e) -> sseEmitters.remove(receiverId));

        return sseEmitter;
    }


    // 알림 조회
    @Override
    public List<NotificationRespDto> getNotification(CustomUserDetails userDetails) {
        // 로그인 한 유저 id 가져오기
        Long memberId = userDetails.getMemberId();

        // 알림 가져오기
        List<Notification> notifications = notificationRepository.findByMember_MemberIdAndIsReadOrderByCreateDateDesc(memberId, false);

        return notifications.stream()
                .map(NotificationRespDto::new)
                .toList();
    }

    // 알림 읽음 처리
    @Override
    public void markAsRead(Long notificationId, CustomUserDetails userDetails) {
        // 알림 가져오기
        Notification notification = notificationRepository.findByNotificationIdAndIsRead(notificationId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 로그인 한 유저 id 가져오기
        Long receiverId = userDetails.getMemberId();

        // DB 업데이트
        notification.updateIsRead();
        notificationRepository.save(notification);

        // Map 에서 memberId 로 사용자 검색
        if (sseEmitters.containsKey(receiverId)) {
            SseEmitter sseEmitter = sseEmitters.get(receiverId);
            // 알림 메세지 전송 및 해제
            try {
                // 알림 개수 감소
                notificationCounts.put(receiverId, notificationCounts.get(receiverId) - 1);

                // 알림 개수 전송
                Map<String, Integer> countData = new HashMap<>();
                countData.put("notificationCount", notificationCounts.get(receiverId));

                sseEmitter.send(SseEmitter.event().name("notification").data(countData));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }
    }

    // 가입 신청 알림
    @Override
    public void notifyPending(Long crewId) {
        // 모임 정보 조회
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 모임명
        String crewName = crew.getName();

        // url 생성
        String url = "/crew/" + crewId + "/manage-member";

        // 전송할 message 생성
        String message = "[" + crewName + "]에 가입 신청이 왔습니다.";

        // 모임의 모임장 또는 운영진 id 값 가져오기
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );
        List<CrewMember> crewMembers = crewMemberRepository.findByCrewAndRoleInOrderByRoleDesc(crew, roles);

        // 멤버에게 모두 전송
        for (CrewMember crewMember : crewMembers) {
            Long receiverId = crewMember.getMember().getMemberId();
            Member receiver = memberRepository.findByMemberIdAndStatus(receiverId, 1)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            // 알림 DB 저장
            Notification notification = Notification.builder()
                    .message(message)
                    .url(url)
                    .type(NotificationType.PENDING)
                    .isRead(false)
                    .member(receiver)
                    .build();
            Notification save = notificationRepository.save(notification);

            // Map 에서 memberId 로 사용자 검색
            if (sseEmitters.containsKey(receiverId)) {
                SseEmitter sseEmitter = sseEmitters.get(receiverId);
                // 알림 메세지 전송 및 해제
                try {
                    Map<String, String> eventData = new HashMap<>();
                    eventData.put("notificationId", save.getNotificationId().toString());
                    eventData.put("message", message);
                    eventData.put("url", url);
                    eventData.put("type", save.getType().toString());

                    sseEmitter.send(SseEmitter.event().name("notification").data(eventData));

                    // 알림 개수 증가
                    notificationCounts.put(receiverId, notificationCounts.get(receiverId) + 1);

                    // 알림 개수 전송
                    Map<String, Integer> countData = new HashMap<>();
                    countData.put("notificationCount", notificationCounts.get(receiverId));

                    sseEmitter.send(SseEmitter.event().name("notification").data(countData));
                } catch (Exception e) {
                    sseEmitters.remove(receiverId);
                }
            }
        }

    }

    // 가입 신청 알림
    @Override
    public void notifyApproval(Long crewId, Long receiverId) {
        // 모임 정보 조회
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 회원 조회
        Member receiver = memberRepository.findByMemberIdAndStatus(receiverId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 모임명
        String crewName = crew.getName();

        // url 생성
        String url = "/crew/" + crewId;

        // 전송할 message 생성
        String message = "[" + crewName + "]에 가입되었습니다.";

        // 알림 DB 저장
        Notification notification = Notification.builder()
                .message(message)
                .url(url)
                .type(NotificationType.APPROVAL)
                .isRead(false)
                .member(receiver)
                .build();
        Notification save = notificationRepository.save(notification);

        // Map 에서 memberId 로 사용자 검색
        if (sseEmitters.containsKey(receiverId)) {
            SseEmitter sseEmitter = sseEmitters.get(receiverId);
            // 알림 메세지 전송 및 해제
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("notificationId", save.getNotificationId().toString());
                eventData.put("message", message);
                eventData.put("url", url);
                eventData.put("type", save.getType().toString());

                sseEmitter.send(SseEmitter.event().name("notification").data(eventData));

                // 알림 개수 증가
                notificationCounts.put(receiverId, notificationCounts.get(receiverId) + 1);

                // 알림 개수 전송
                Map<String, Integer> countData = new HashMap<>();
                countData.put("notificationCount", notificationCounts.get(receiverId));

                sseEmitter.send(SseEmitter.event().name("notification").data(countData));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }


    }

    // 댓글 알림
    @Override
    public void notifyComment(Long crewId, Long boardId) {
        Board board = boardRepository.findBoardByBoardIdAndStatusNotAndCrew_CrewId(boardId, 0, crewId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        // 게시글 작성자 id 값 추출
        Member receiver = board.getCrewMember().getMember();
        Long receiverId = board.getCrewMember().getMember().getMemberId();

        // 모임명
        String crewName = board.getCrew().getName();

        // 게시글 이름
        String boardTitle = board.getTitle();

        // url 생성
        String url = "/crew/" + crewId + "/board/" + boardId;

        // 전송할 message 생성
        String message = "[" + crewName + "]의 게시글 \"" + boardTitle + "\"에 댓글이 달렸습니다.";

        // 알림 DB 저장
        Notification notification = Notification.builder()
                .message(message)
                .url(url)
                .type(NotificationType.COMMENT)
                .isRead(false)
                .member(receiver)
                .build();
        Notification save = notificationRepository.save(notification);

        // Map 에서 memberId 로 사용자 검색
        if (sseEmitters.containsKey(receiverId)) {
            SseEmitter sseEmitter = sseEmitters.get(receiverId);
            // 알림 전송 및 해제
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("notificationId", save.getNotificationId().toString());
                eventData.put("message", message);
                eventData.put("url", url);
                eventData.put("type", save.getType().toString());

                sseEmitter.send(SseEmitter.event().name("notification").data(eventData));

                // 알림 개수 증가
                notificationCounts.put(receiverId, notificationCounts.get(receiverId) + 1);

                // 알림 개수 전송
                Map<String, Integer> countData = new HashMap<>();
                countData.put("notificationCount", notificationCounts.get(receiverId));

                sseEmitter.send(SseEmitter.event().name("notification").data(countData));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }
    }

    // 1대1 채팅 알림
    @Override
    public void notifyChat(Long crewId, Long senderId, Long receiverId, String chat) {
        // 모임 정보 조회
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));

        // 발신자
        Member sender = memberRepository.findByMemberIdAndStatus(senderId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 수신자
        Member receiver = memberRepository.findByMemberIdAndStatus(receiverId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 모임명
        String crewName = crew.getName();

        // url 생성
        String url = "/crew/" + crewId + "/chat/" + receiverId + "/" + senderId;

        // 전송할 message 생성
        String message = "[" + crewName + "]에 새로운 채팅이 왔습니다. : " + sender.getNickname() + " \"" + chat + "\"";

        // 알림 DB 저장
        Notification notification = Notification.builder()
                .message(message)
                .url(url)
                .type(NotificationType.CHAT)
                .isRead(false)
                .member(receiver)
                .build();
        Notification save = notificationRepository.save(notification);

        // Map 에서 memberId 로 사용자 검색
        if (sseEmitters.containsKey(receiverId)) {
            SseEmitter sseEmitter = sseEmitters.get(receiverId);
            // 알림 전송 및 해제
            try {
                Map<String, String> eventData = new HashMap<>();
                eventData.put("notificationId", save.getNotificationId().toString());
                eventData.put("message", message);
                eventData.put("url", url);
                eventData.put("type", save.getType().toString());

                sseEmitter.send(SseEmitter.event().name("notification").data(eventData));

                // 알림 개수 증가
                notificationCounts.put(receiverId, notificationCounts.get(receiverId) + 1);

                // 알림 개수 전송
                Map<String, Integer> countData = new HashMap<>();
                countData.put("notificationCount", notificationCounts.get(receiverId));

                sseEmitter.send(SseEmitter.event().name("notification").data(countData));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }
    }

}
