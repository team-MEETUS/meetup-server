package site.mymeetup.meetupserver.chat.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;

import site.mymeetup.meetupserver.chat.dto.ChatDto;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.response.ApiResponse;
import java.time.LocalDateTime;

public interface ChatService {
    Mono<ApiResponse<ChatRespDto>> createChat(Long crewId, ChatDto.ChatSaveReqDto chatSaveReqDto, Long senderId);

    Mono<ApiResponse<ChatRespDto>> createPrivateChat(Long crewId, ChatDto.ChatSaveReqDto chatSaveReqDto, Long senderId);

    Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(Long crewId, Long senderId);

    Flux<ApiResponse<ChatRespDto>> getAllByCrewIdAndSenderIdAndReceiverId(Long crewId, Long senderId, Long receiverId);
}
