package site.mymeetup.meetupserver.chat.service;

import reactor.core.publisher.Flux;
import site.mymeetup.meetupserver.chat.dto.ChatDto;

import java.time.LocalDateTime;

public interface ChatService {
    Flux<ChatDto.ChatFindRespDto> getAllChatByCrewId(Long crewId, LocalDateTime createDate);
}
