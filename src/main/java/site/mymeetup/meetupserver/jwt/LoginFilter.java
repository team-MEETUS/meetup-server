package site.mymeetup.meetupserver.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper; // ObjectMapper 추가

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/v1/members/login");
        this.jwtUtil = jwtUtil;
        this.objectMapper = new ObjectMapper(); // ObjectMapper 초기화
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = null;
        String password = null;

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            JSONObject jsonObject = new JSONObject(json);
            username = jsonObject.getString("phone");
            password = jsonObject.getString("password");
        } catch (IOException | JSONException e) {
            log.error("Error reading JSON data", e);
        }

        log.info("Attempting to authenticate user: {}", username);
        log.debug("Password provided: {}", password);

        // 추가적인 확인
        if (username == null || username.isEmpty()) {
            log.warn("Username is empty or null");
        }
        if (password == null || password.isEmpty()) {
            log.warn("Password is empty or null");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
        return authenticationManager.authenticate(token);
    }

    // 로그인 성공 시 실행하는 메소드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        log.info("Authentication successful");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        Long memberId = customUserDetails.getMemberId();
        int status = customUserDetails.getStatus();

        if (status != 1) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("data", null);
            errorResponse.put("error", Map.of("code", "LOGIN_DENIED", "message", "로그인이 불가능한 상태입니다."));
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // token 유효시간 7일
        String token = jwtUtil.createJwt(role, memberId, 7 * 24 * 60 * 60 * 1000L);

        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("data", Map.of("memberId", memberId, "accessToken", token));
        successResponse.put("error", null);
        response.getWriter().write(objectMapper.writeValueAsString(successResponse));
    }

    // 로그인 실패 시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("Authentication failed");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("error", Map.of("code", "LOGIN_FAIL", "message", "로그인 실패"));
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}