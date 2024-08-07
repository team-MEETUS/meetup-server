package site.mymeetup.meetupserver.chat.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;
import site.mymeetup.meetupserver.response.ApiResponse;
import java.time.LocalDateTime;

public interface ChatService {
    Mono<ApiResponse<ChatRespDto>> createChat(ChatRespDto chatRespDto);

    Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(Long crewId, LocalDateTime createDate);

    Flux<ApiResponse<ChatRespDto>> getAllByCrewIdAndSenderIdAndReceiverId(Long crewId, Long senderId, Long receiverId);
}
