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

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        response.setContentType("application/json; charset=UTF-8");

        // 로그인 요청 URL 확인 (예: "/login" 또는 "/api/login")
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        if (method.equals("GET") && (requestURI.matches("/api/v1/crews/\\d+") ||
                                     requestURI.equals("/api/v1/crews") ||
                                     requestURI.matches("/api/v1/crews/\\d+/members") ||
                                     requestURI.equals("/api/v1/geos") ||
                                     requestURI.equals("/api/v1/interestBigs") ||
                                     requestURI.matches("/api/v1/interestBigs/\\d+/interestSmalls") ||
                                     requestURI.matches("/api/v1/members/\\d+") ||
                                     requestURI.matches("/api/v1/crews/\\d+/albums"))) {
            // 로그인 요청인 경우, 필터를 계속 진행
            filterChain.doFilter(request, response);
            return;
        }
        if (method.equals("POST") && (requestURI.equals("/api/v1/login") ||
                                      requestURI.equals("/api/v1/members/join"))) {
            // 로그인 요청인 경우, 필터를 계속 진행
            filterChain.doFilter(request, response);
            return;
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"INVALID_TOKEN\", \"message\": \"토큰이 없습니다.\"}}");
            return;
        }

        String token = authorization.split(" ")[1];

        try {
            if (jwtUtil.isExpired(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"INVALID_TOKEN\", \"message\": \"로그인 유효기간이 끝났습니다.\"}}");
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"INVALID_TOKEN\", \"message\": \"토큰이 만료되었습니다.\"}}");
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"INVALID_TOKEN\", \"message\": \"Invalid JWT token\"}}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
