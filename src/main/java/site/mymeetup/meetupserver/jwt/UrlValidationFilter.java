package site.mymeetup.meetupserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import site.mymeetup.meetupserver.exception.CustomException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlValidationFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public UrlValidationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final List<String> validUrls = Arrays.asList(
            "/api/v1/crews/[0-9]+/albums", "/api/v1/crews/[0-9]+/albums/[0-9]+", "/api/v1/crews/[0-9]+/albums/[0-9]+/likes",
            "/api/v1/crews/[0-9]+/boards", "/api/v1/crews/[0-9]+/boards/[0-9]+", "/api/v1/crews/[0-9]+/boards/[0-9]+/pin", "/api/v1/crews/[0-9]+/boards/images",
            "/api/v1/crews/[0-9]+/boards/[0-9]+/comments", "/api/v1/crews/[0-9]+/boards/[0-9]+/comments/[0-9]+",
            "/api/v1/crews/[0-9]+/chats", "/ws/.*", "/ws",
            "/api/v1/crews", "/api/v1/crews/[0-9]+", "/api/v1/crews/me",
            "/api/v1/crews/interests", "/api/v1/crews/active", "/api/v1/crews/new", "/api/v1/crews/search",
            "/api/v1/crews/[0-9]+/members/me", "/api/v1/crews/[0-9]+/members", "/api/v1/crews/[0-9]+/likes",
            "/api/v1/notifications/subscribe", "/api/v1/notifications", "/api/v1/notifications/[0-9]+",
            "/api/v1/geos", "/api/v1/interestBigs", "/api/v1/interestBigs/[0-9]+/interestSmalls",
            "/api/v1/crews/[0-9]+/meetings", "/api/v1/crews/[0-9]+/meetings/[0-9]+",
            "/api/v1/login", "/api/v1/members/[0-9]+", "/api/v1/members/join", "/api/v1/members/info","/api/v1/members/phoneCheck", "/api/v1/members/[0-9]+/interests"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setContentType("application/json; charset=UTF-8");
        String requestUrl = request.getRequestURI();

        if (!isValidUrl(requestUrl)) {
            sendErrorResponse(response, "E40401", "처리할 수 없는 요청입니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidUrl(String requestUri) {
        // URL 검증 로직
        return validUrls.stream().anyMatch(requestUri::matches);
    }

    private void sendErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("error", Map.of("code", code, "message", message));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
