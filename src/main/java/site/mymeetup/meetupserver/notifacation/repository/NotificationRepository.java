package site.mymeetup.meetupserver.notifacation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.notifacation.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMember_MemberIdAndIsReadOrderByCreateDateDesc(Long memberId, Boolean isRead);
}
