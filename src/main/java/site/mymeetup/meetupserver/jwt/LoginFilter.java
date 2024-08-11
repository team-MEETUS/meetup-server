package site.mymeetup.meetupserver.jwt;

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
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/v1/login");
        this.jwtUtil = jwtUtil;
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
        log.debug("Password provided: {}", password); // 비밀번호는 보안상 로그에 남기지 않는 것이 좋습니다.

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
        System.out.println("success");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        Long memberId = customUserDetails.getMemberId();
        int status = customUserDetails.getStatus();

        if (status != 1) {
            response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"LOGIN_DENIED\", \"message\": \"로그인이 불가능한 상태입니다.\"}}");
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // token 유효시간 5시간
        String token = jwtUtil.createJwt(username, role, memberId, 5*60*60*1000L);

        response.addHeader("Authorization", "Bearer " + token);
        response.getWriter().write("{\"success\": true, \"data\": {\"memberId\": " + memberId + "}, \"error\": null}");
    }

    // 로그인 실패 시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        System.out.println("fail");
        response.getWriter().write("{\"success\": false, \"data\": null, \"error\": {\"code\": \"LOGIN_FAIL\", \"message\": \"로그인 실패\"}}");
    }
}
