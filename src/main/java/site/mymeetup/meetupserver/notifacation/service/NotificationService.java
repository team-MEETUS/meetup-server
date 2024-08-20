package site.mymeetup.meetupserver.notifacation.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

public interface NotificationService {
    SseEmitter createEmitter(CustomUserDetails userDetails);

    void notifyComment(Long crewId, Long boardId);
}
