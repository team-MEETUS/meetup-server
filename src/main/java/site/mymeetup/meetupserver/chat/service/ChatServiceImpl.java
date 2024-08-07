package site.mymeetup.meetupserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;

import site.mymeetup.meetupserver.chat.entity.Chat;
import site.mymeetup.meetupserver.chat.repository.ChatRepository;
//import site.mymeetup.meetupserver.crew.repository.CrewRepository;
//import site.mymeetup.meetupserver.exception.CustomException;
//import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public Mono<ApiResponse<ChatRespDto>> createChat(ChatRespDto chatRespDto) {

        if (chatRespDto.getSenderId() == null || chatRespDto.getMessage() == null) {
            return Mono.just(ApiResponse.error(ErrorCode.CHAT_NOT_FOUND));
        }

        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .message(chatRespDto.getMessage())
                .senderId(chatRespDto.getSenderId())
                .receiverId(chatRespDto.getReceiverId())
                .createDate(LocalDateTime.now())
                .crewId(chatRespDto.getCrewId())
                .build();

        return chatRepository.save(chat)
                .doOnNext(savedMessage -> messagingTemplate.convertAndSend("/topic/messages", savedMessage))
                .map(savedChat -> ApiResponse.success(ChatRespDto.builder().chat(chat).build()));
    }


    @Override
    public Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(Long crewId, LocalDateTime createDate) {
        return chatRepository.findAllByCrewIdAndCreateDateAfter(crewId, createDate)
                .map(chat -> {
                    ChatRespDto chatRespDto = ChatRespDto.builder().chat(chat).build();
                    return ApiResponse.success(chatRespDto);
                })
                .switchIfEmpty(chat -> Flux.just(ApiResponse.success(null))); // 데이터가 없는 경우를 처리
    }

    @Override
    public Flux<ApiResponse<ChatRespDto>> getAllByCrewIdAndSenderIdAndReceiverId(Long crewId, Long senderId, Long receiverId) {
        return chatRepository.findAllByCrewIdAndSenderIdAndReceiverId(crewId, senderId, receiverId)
                .map(chat -> {
                    ChatRespDto chatRespDto = ChatRespDto.builder().chat(chat).build();
                    return ApiResponse.success(chatRespDto);
                })
                .switchIfEmpty(chat -> Flux.just(ApiResponse.success(null))); // 데이터가 없는 경우를 처리
    }

}
