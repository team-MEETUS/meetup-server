package site.mymeetup.meetupserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import site.mymeetup.meetupserver.chat.dto.ChatDto;
import site.mymeetup.meetupserver.chat.entity.Chat;
import site.mymeetup.meetupserver.chat.repository.ChatRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    @Override
    public Flux<ChatDto.ChatFindRespDto> getAllChatByCrewId(Long crewId, LocalDateTime createDate) {
        return chatRepository.findAllByCrewIdAndCreateDateAfter(crewId, createDate)
                .map(chat -> ChatDto.ChatFindRespDto.builder()
                        .chat(chat)
                        .build());
    }
}
