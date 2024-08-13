package site.mymeetup.meetupserver.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.role.Role;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper; // ObjectMapper 추가

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = new ObjectMapper(); // ObjectMapper 초기화
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        response.setContentType("application/json; charset=UTF-8");

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String status = request.getParameter("status");

        // 로그인 및 특정 GET 요청은 필터를 계속 진행
        if (method.equals("GET") && (requestURI.equals("/api/v1/geos") ||
                                     requestURI.equals("/api/v1/interestBigs") ||
                                     requestURI.matches("/api/v1/interestBigs/\\d+/interestSmalls") ||
                                     requestURI.matches("/api/v1/members/\\d+") ||
                                     requestURI.equals("/api/v1/crews") ||
                                     requestURI.matches("/api/v1/crews/\\d+") ||
                                    (requestURI.matches("/api/v1/crews/\\d+/members") && "members".equals(status)) ||
                                     requestURI.matches("/api/v1/crews/\\d+/meetings") ||
                                     requestURI.matches("/api/v1/crews/\\d+/meetings/\\d+") ||
                                     requestURI.matches("/api/v1/crews/\\d+/albums") ||
                                     requestURI.matches("/api/v1/crews/\\d+/boards"))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (method.equals("POST") && (requestURI.equals("/api/v1/login") ||
                requestURI.equals("/api/v1/members/join"))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            sendErrorResponse(response, "INVALID_TOKEN", "토큰이 없습니다.");
            return;
        }

        String token = authorization.split(" ")[1];

        try {
            if (jwtUtil.isExpired(token)) {
                sendErrorResponse(response, "INVALID_TOKEN", "로그인 유효기간이 끝났습니다.");
                return;
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            Long memberId = jwtUtil.getMemberId(token);

            Member member = Member.builder()
                    .role(Role.valueOf(role))
                    .memberId(memberId)
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, "INVALID_TOKEN", "토큰이 만료되었습니다.");
            return;
        } catch (Exception e) {
            sendErrorResponse(response, "INVALID_TOKEN", "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("error", Map.of("code", code, "message", message));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
