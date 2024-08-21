package site.mymeetup.meetupserver.notifacation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.notifacation.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 읽지 않은 알림 개수 조회
    int countByMember_MemberIdAndIsRead(Long memberId, Boolean isRead);

    // 알림 조회
    List<Notification> findByMember_MemberIdAndIsReadOrderByCreateDateDesc(Long memberId, Boolean isRead);

    // 특정 알림 조회
    Optional<Notification> findByNotificationIdAndIsRead(Long notificationId, Boolean isRead);
}
