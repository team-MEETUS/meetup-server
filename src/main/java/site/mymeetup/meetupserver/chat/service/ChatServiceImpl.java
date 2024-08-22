package site.mymeetup.meetupserver.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatRespDto;
import static site.mymeetup.meetupserver.chat.dto.ChatDto.ChatSaveReqDto;
import site.mymeetup.meetupserver.chat.entity.Chat;
import site.mymeetup.meetupserver.chat.repository.ChatRepository;
import site.mymeetup.meetupserver.crew.entity.Crew;
import site.mymeetup.meetupserver.crew.entity.CrewMember;
import site.mymeetup.meetupserver.crew.repository.CrewMemberRepository;
import site.mymeetup.meetupserver.crew.repository.CrewRepository;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;
import site.mymeetup.meetupserver.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    @Override
    public Mono<ApiResponse<ChatRespDto>> createChat(Long crewId, ChatSaveReqDto chatSaveReqDto, Long senderId) {
        if (senderId == null || chatSaveReqDto.getMessage() == null) {
            return Mono.just(ApiResponse.error(ErrorCode.CHAT_NOT_FOUND));
        }

        // 해당 유저가 존재하는지 검증
        Member member = memberRepository.findByMemberIdAndStatus(senderId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
        // 해당 유저가 모임원인지 검증
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        CrewMember crewMember = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .message(chatSaveReqDto.getMessage())
                .senderId(senderId)
                .receiverId(chatSaveReqDto.getReceiverId())
                .createDate(LocalDateTime.now())
                .crewId(crewId)
                .build();

        return chatRepository.save(chat)
                .doOnNext(savedMessage -> messagingTemplate.convertAndSend("/topic/messages/group" + crewId, savedMessage))
                .map(savedChat -> ApiResponse.success(ChatRespDto.builder().chat(chat).member(memberRepository.findByMemberIdAndStatus(senderId, 1).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))).crewMemberRole(crewMember.getRole()).build()));
    }

    @Override
    public Mono<ApiResponse<ChatRespDto>> createPrivateChat(Long crewId, ChatSaveReqDto chatSaveReqDto, Long senderId) {
        if (senderId == null || chatSaveReqDto.getMessage() == null) {
            return Mono.just(ApiResponse.error(ErrorCode.CHAT_NOT_FOUND));
        }

        // 해당 유저가 존재하는지 검증
        Member member = memberRepository.findByMemberIdAndStatus(senderId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
        // 해당 유저가 모임원인지 검증
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        CrewMember crewMember = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        Long receiverId = chatSaveReqDto.getReceiverId();

        Chat chat = Chat.builder()
                .id(UUID.randomUUID().toString())
                .message(chatSaveReqDto.getMessage())
                .senderId(senderId)
                .receiverId(chatSaveReqDto.getReceiverId())
                .createDate(LocalDateTime.now())
                .crewId(crewId)
                .build();

        return chatRepository.save(chat)
                .doOnNext(savedMessage -> messagingTemplate.convertAndSend("/topic/messages/private/" + crewId + "/" + receiverId, savedMessage))
                .map(savedChat -> ApiResponse.success(ChatRespDto.builder().chat(chat).member(memberRepository.findByMemberIdAndStatus(senderId, 1).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))).crewMemberRole(crewMember.getRole()).build()));
    }

    @Override
    public Flux<ApiResponse<ChatRespDto>> getAllChatByCrewId(Long crewId, Long senderId) {
        // 해당 유저가 존재하는지 검증
        Member member = memberRepository.findByMemberIdAndStatus(senderId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        // 해당 모임이 존재하는지 검증
        Crew crew = crewRepository.findByCrewIdAndStatus(crewId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_NOT_FOUND));
        // 해당 유저가 모임원인지 검증
        List<CrewMemberRole> roles = Arrays.asList(
                CrewMemberRole.MEMBER,
                CrewMemberRole.ADMIN,
                CrewMemberRole.LEADER
        );

        CrewMember crewMember = crewMemberRepository.findByCrewAndMemberAndRoleIn(crew, member, roles)
                .orElseThrow(() -> new CustomException(ErrorCode.CREW_MEMBER_NOT_FOUND));

        return chatRepository.findAllByCrewIdAndCreateDateAfter(crewId, crewMember.getCreateDate())
                .map(chat -> {
                    ChatRespDto chatRespDto = ChatRespDto.builder()
                            .chat(chat)
                            .member(memberRepository.findByMemberIdAndStatus(chat.getSenderId(), 1).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)))
                            .crewMemberRole(crewMemberRepository.findByCrew_CrewIdAndMember_MemberId(crewId, chat.getSenderId()).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)).getRole()).build();
                    return ApiResponse.success(chatRespDto);
                })
                .switchIfEmpty(chat -> Flux.just(ApiResponse.success(null))); // 데이터가 없는 경우를 처리
    }

    @Override
    public Flux<ApiResponse<ChatRespDto>> getAllByCrewIdAndSenderIdAndReceiverId(Long crewId, Long senderId, Long receiverId) {
        Member member = memberRepository.findByMemberIdAndStatus(senderId, 1)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return chatRepository.findAllByCrewIdAndSenderIdAndReceiverId(crewId, senderId, receiverId)
                .map(chat -> {
                    ChatRespDto chatRespDto = ChatRespDto.builder().chat(chat).member(memberRepository.findByMemberIdAndStatus(chat.getSenderId(), 1).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND))).build();
                    return ApiResponse.success(chatRespDto);
                })
                .switchIfEmpty(chat -> Flux.just(ApiResponse.success(null))); // 데이터가 없는 경우를 처리
    }

}
