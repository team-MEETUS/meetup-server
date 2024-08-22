package site.mymeetup.meetupserver.chat.control;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatSaveReqDto;
import site.mymeetup.meetupserver.chat.service.ChatService;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews/{crewId}")
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/send/{crewId}")
    @SendTo("/topic/messages/{crewId}")
    public Mono<ApiResponse<ChatRespDto>> sendMessage(@DestinationVariable("crewId") Long crewId,
                                                      ChatSaveReqDto chatSaveReqDto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatService.createChat(crewId, chatSaveReqDto, userDetails.getMemberId());
    }

    // 채팅 내역 조회
    @GetMapping("/chats")
    public Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(@PathVariable Long crewId,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails,
                                                             @RequestParam(required = false) Long receiverId) {
        if (receiverId == null) {
            return chatService.getAllChatByCrewId(crewId, userDetails.getMemberId());
        } else {
            return chatService.getAllByCrewIdAndSenderIdAndReceiverId(crewId, userDetails.getMemberId(), receiverId);
        }
    }
}
