package site.mymeetup.meetupserver.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.role.Role;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> tokenList = accessor.getNativeHeader("Authorization");
            if (tokenList != null && !tokenList.isEmpty()) {
                String token = tokenList.get(0); // 첫 번째 토큰을 가져옵니다.

                // "Bearer " 접두사를 제거합니다.
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                // JWT 검증 및 사용자 정보 설정
                try {
                    if (jwtProvider.isExpired(token)) {
                        throw new IllegalArgumentException("로그인 유효기간이 끝났습니다.");
                    }

                    String username = jwtProvider.getUsername(token);
                    String role = jwtProvider.getRole(token);
                    Long memberId = jwtProvider.getMemberId(token);

                    Member member = Member.builder()
                            .role(Role.valueOf(role))
                            .memberId(memberId)
                            .build();

                    CustomUserDetails customUserDetails = new CustomUserDetails(member);

                    // Authentication 객체 생성
                    Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
                    accessor.setUser(authToken);
                } catch (ExpiredJwtException e) {
                    throw new IllegalArgumentException("토큰이 만료되었습니다.");
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.NOT_FOUND_DEPT);
                }
            } else {
                throw new IllegalArgumentException("Authorization header is missing");
            }
        }
        return message;
    }
}

