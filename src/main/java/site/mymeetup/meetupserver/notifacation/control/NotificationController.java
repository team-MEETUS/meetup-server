package site.mymeetup.meetupserver.notifacation.control;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.notifacation.service.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 구독
    @GetMapping("/subscribe")
    public SseEmitter subscribeNotification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.createEmitter(userDetails);
    }

}
