package site.mymeetup.meetupserver.notifacation.control;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.notifacation.service.NotificationService;
import site.mymeetup.meetupserver.response.ApiResponse;
import static site.mymeetup.meetupserver.notifacation.dto.NotificationDto.NotificationRespDto;

import java.util.List;


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

    // 알림 조회
    @GetMapping
    public ApiResponse<List<NotificationRespDto>> getNotification(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(notificationService.getNotification(userDetails));
    }

    // 알림 읽음 처리
    @PutMapping("/{notificationId}")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAsRead(notificationId, userDetails);
        return ApiResponse.success(null);
    }
}
