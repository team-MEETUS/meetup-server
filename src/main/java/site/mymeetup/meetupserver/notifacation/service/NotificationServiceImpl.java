package site.mymeetup.meetupserver.notifacation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final Map<Long, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    // SSE 연결
    @Override
    public SseEmitter createEmitter(CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();

        // sseEmitter 객체 생성
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE); // 타임아웃 설정

        // 연결
        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
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

}
