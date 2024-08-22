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

    // 1:1 채팅 메시지 전송
    @MessageMapping("/send/private/{crewId}/{receiverId}")
    @SendTo("/topic/messages/private/{crewId}/{receiverId}")
    public Mono<ApiResponse<ChatRespDto>> sendPrivateMessage(@DestinationVariable("crewId") Long crewId,
                                                             @DestinationVariable("receiverId") Long receiverId,
                                                             ChatSaveReqDto chatSaveReqDto,
                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return chatService.createPrivateChat(crewId, chatSaveReqDto, userDetails.getMemberId());
    }

    @MessageMapping("/send/group/{crewId}")
    @SendTo("/topic/messages/group/{crewId}")
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
