package site.mymeetup.meetupserver.chat.control;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;
import site.mymeetup.meetupserver.chat.entity.Chat;
import site.mymeetup.meetupserver.chat.service.ChatService;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crews")
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Mono<ApiResponse<ChatRespDto>> sendMessage(ChatRespDto chatRespDto,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getMemberId());
        return chatService.createChat(chatRespDto);
    }

    // 채팅 내역 조회
    @GetMapping("/{crewId}/chats")
    public Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(@PathVariable Long crewId,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @RequestParam(required = false) Long receiverId) {
        System.out.println(receiverId);
        if (receiverId == null) {
            return chatService.getAllChatByCrewId(crewId, userDetails.getMemberId());
        } else {
            return chatService.getAllByCrewIdAndSenderIdAndReceiverId(crewId, userDetails.getMemberId(), receiverId);
        }
    }
}
