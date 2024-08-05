package site.mymeetup.meetupserver.chat.control;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.mymeetup.meetupserver.chat.dto.ChatDto;
import site.mymeetup.meetupserver.chat.service.ChatService;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {
    private final ChatService chatService;

    // 모임 단체 채팅 내역 조회
    @GetMapping("/{crewId}")
    public Flux<ApiResponse<?>> getAllChatByCrewId(@PathVariable Long crewId,
                                                   @RequestParam LocalDateTime createDate) {
        return chatService.getAllChatByCrewId(crewId, createDate)
                .collectList()
                .flatMapMany(chatFindRespDtos -> {
                    if (chatFindRespDtos.isEmpty()) {
                        return Flux.just(ApiResponse.success(null));
                    }
                    return Flux.just(ApiResponse.success(chatFindRespDtos));
                })
                .onErrorReturn(ApiResponse.error(ErrorCode.CHAT_NOT_FOUND));
    }
}
