package site.mymeetup.meetupserver.notifacation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.notifacation.entity.Notification;
import site.mymeetup.meetupserver.notifacation.type.NotificationType;

import java.time.LocalDateTime;

public class NotificationDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NotificationRespDto {
        private Long notificationId;
        private String message;
        private String url;
        private NotificationType type;
        private LocalDateTime createDate;

        @Builder
        public NotificationRespDto(Notification notification) {
            this.notificationId = notification.getNotificationId();
            this.message = notification.getMessage();
            this.url = notification.getUrl();
            this.type = notification.getType();
            this.createDate = notification.getCreateDate();
        }
    }

}
