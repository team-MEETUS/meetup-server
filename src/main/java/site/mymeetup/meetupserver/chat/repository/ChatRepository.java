package site.mymeetup.meetupserver.chat.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.mymeetup.meetupserver.chat.entity.Chat;

import java.time.LocalDateTime;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

    // 모임 단체 채팅 조회
    @Query("{ 'crewId': ?0, 'createDate': { $gt: ?1 }, 'receiverId': null }")
    Flux<Chat> findAllByCrewIdAndCreateDateAfter(Long crewId, LocalDateTime createDate);

    // 모임 1대1 채팅 조회
    @Query("{$and: [ {'crewId': ?0}, {$or: [ {$and: [ {'senderId': ?1}, {'receiverId': ?2} ]}, {$and: [ {'senderId': ?2}, {'receiverId': ?1} ]} ]} ]}")
    Flux<Chat> findAllByCrewIdAndSenderIdAndReceiverId(Long crewId, Long senderId, Long receiverId);

    // 특정 모임의 가장 마지막 채팅 조회
    Mono<Chat> findFirstByCrewIdAndReceiverIdIsNullOrderByCreateDateDesc(Long crewId);
}
