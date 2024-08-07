package site.mymeetup.meetupserver.chat.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import site.mymeetup.meetupserver.chat.entity.Chat;

import java.time.LocalDateTime;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat> findAllByCrewIdAndCreateDateAfter(Long crewId, LocalDateTime createDate);
}
