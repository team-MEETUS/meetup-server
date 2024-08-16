package site.mymeetup.meetupserver.config;

import jakarta.security.auth.message.AuthException;
import lombok.SneakyThrows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.util.Objects;

public class WebSocketInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtProvider;

    public WebSocketInterceptor(JWTUtil jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @SneakyThrows
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (Objects.equals(accessor.getCommand(), StompCommand.CONNECT)
                || Objects.equals(accessor.getCommand(), StompCommand.SEND)) { // 문제 발생 예상 지/점
            String token = removeBrackets(String.valueOf(accessor.getNativeHeader("Authorization")));
            try {
                Authentication authentication = jwtProvider.getAuthentication(token);
                Long accountId = ((CustomUserDetails)authentication.getPrincipal()).getMemberId();
                accessor.addNativeHeader("AccountId", String.valueOf(accountId));
            } catch (Exception e) {
            }
        }

        return message;
    }

    private String removeBrackets(String token) {
        if (token.startsWith("[") && token.endsWith("]")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }
}
