package site.mymeetup.meetupserver.notifacation.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.notifacation.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
