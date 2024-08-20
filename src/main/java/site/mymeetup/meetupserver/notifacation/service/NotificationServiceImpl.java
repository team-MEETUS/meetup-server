package site.mymeetup.meetupserver.notifacation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.board.entity.Board;
import site.mymeetup.meetupserver.board.repository.BoardRepository;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.notifacation.entity.Notification;
import site.mymeetup.meetupserver.notifacation.repository.NotificationRepository;
import site.mymeetup.meetupserver.notifacation.type.NotificationType;
import static site.mymeetup.meetupserver.notifacation.dto.NotificationDto.NotificationRespDto;

import java.io.IOException;
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
    private final BoardRepository boardRepository;

    // SSE 연결
    @Override
    public SseEmitter createEmitter(CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();

        // sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃 설정

        // 연결
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));

            // 초기 알림 개수 조회 및 map 에 저장
            int initialNotificationCount = notificationRepository.countByMember_MemberIdAndIsRead(memberId, false);
            notificationCounts.put(memberId, initialNotificationCount);

            // 알림 개수 전송
            sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(memberId)));
        } catch (IOException e) {
            log.error("Error while sending SSE connection event for memberId {}: {}", memberId, e.getMessage(), e);
        }

        // 저장
        sseEmitters.put(memberId, sseEmitter);

        // 연결 종료
        sseEmitter.onCompletion(() -> sseEmitters.remove(memberId));
        sseEmitter.onTimeout(() -> sseEmitters.remove(memberId));
        sseEmitter.onError((e) -> sseEmitters.remove(memberId));

        return sseEmitter;
    }

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
        String message = "<strong>" + crewName + "</strong>의 게시글 \"" + boardTitle + "\"에 댓글이 달렸습니다.";

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

                sseEmitter.send(SseEmitter.event().name("addComment").data(eventData));

                // 알림 개수 증가
                notificationCounts.put(receiverId, notificationCounts.get(receiverId) + 1);

                // 현재 알림 개수 전송
                sseEmitter.send(SseEmitter.event().name("notificationCount").data(notificationCounts.get(receiverId)));
            } catch (Exception e) {
                sseEmitters.remove(receiverId);
            }
        }
    }

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

}
