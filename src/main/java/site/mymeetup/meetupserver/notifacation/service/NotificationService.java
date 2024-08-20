package site.mymeetup.meetupserver.notifacation.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.util.List;

import static site.mymeetup.meetupserver.notifacation.dto.NotificationDto.NotificationRespDto;

public interface NotificationService {
    SseEmitter createEmitter(CustomUserDetails userDetails);

    void notifyComment(Long crewId, Long boardId);

    List<NotificationRespDto> getNotification(CustomUserDetails userDetails);

    void markAsRead(Long notificationId, CustomUserDetails userDetails);
}
