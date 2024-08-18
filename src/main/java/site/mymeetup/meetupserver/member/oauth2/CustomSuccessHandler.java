package site.mymeetup.meetupserver.member.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public CustomSuccessHandler(JWTUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    // 로그인 성공시 동작
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            response.setContentType("application/json; charset=UTF-8");

            // OAuth2User
            CustomUserDetails customOAuth2User = (CustomUserDetails) authentication.getPrincipal();

            // username = phone
            String username = customOAuth2User.getUsername();

            // role값
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
            GrantedAuthority auth = iterator.next();
            String role = auth.getAuthority();

            // memberId
            Long memberId = customOAuth2User.getMemberId();

            // JWT 생성
            String token = jwtUtil.createJwt(role, memberId, 60 * 60 * 60L);

            // 쿠키 추가
            response.addCookie(createCookie("Authorization", token));

            // 리디렉션
            response.sendRedirect("http://localhost:3000");

        } catch (Exception e) {
            logger.error("Error during authentication success handling", e);
            ErrorCode errorCode = ErrorCode.MEMBER_AUTHENTICATION_FAILED;
            response.setStatus(errorCode.getHttpStatus().value());
            response.getWriter().write("Authentication processing failed. Please try again later.");
        }
    }

    private Cookie createCookie(String key, String value) {
            Cookie cookie = new Cookie(key, value);
            cookie.setMaxAge(60 * 60 * 60);
            //cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            return cookie;
    }
}
