package site.mymeetup.meetupserver.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.jwt.JWTFilter;
import site.mymeetup.meetupserver.jwt.JWTUtil;
import site.mymeetup.meetupserver.jwt.LoginFilter;
import site.mymeetup.meetupserver.member.oauth2.CustomSuccessHandler;
import site.mymeetup.meetupserver.member.service.CustomOAuth2UserService;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        log.info("SecurityConfig initialized with AuthenticationConfiguration");
    }

    // AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        log.info("Creating AuthenticationManager");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.info("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSocketInterceptor webSocketInterceptor(JWTUtil jwtUtil) {
        return new WebSocketInterceptor(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            log.info("Configuring SecurityFilterChain");

            // CSRF 설정
            http.csrf(auth -> auth.disable());
            log.debug("CSRF protection disabled");

            // Form 로그인 방식 비활성화
            http.formLogin(auth -> auth.disable());
            log.debug("Form login disabled");

            // HTTP Basic 인증 비활성화
            http.httpBasic(auth -> auth.disable());
            log.debug("HTTP Basic authentication disabled");

            // oauth2
            http.oauth2Login(oauth2 -> oauth2
                    .successHandler(customSuccessHandler)
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                            .userService(customOAuth2UserService)));

            // 경로별 인가 작업
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.GET,  "/api/v1/geos", "/api/v1/interestBigs", "/api/v1/interestBigs/{interestBigId}/interestSmalls",
                                                      "/api/v1/members/{memberId}",
                                                      "/api/v1/crews", "/api/v1/crews/{crewId}", "/api/v1/crews/{crewId}/members",
                                                      "/api/v1/crews/{crewId}/meetings", "/api/v1/crews/{crewId}/meetings/{meetingId}",
                                                      "/api/v1/crews/{crewId}/albums",
                                                      "/api/v1/crews/{crewId}/boards", "/api/v1/crews/{crewId}/chats", "/api/v1/crews/send").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/members/join", "/api/v1/login", "/api/v1/crews/interests").permitAll()
                    .requestMatchers("/ws/**").permitAll()
                    .anyRequest().authenticated());

            // JWTFilter
            http.addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);
            http.addFilterAt(new JWTFilter(jwtUtil), LoginFilter.class);

            // LoginFilter 추가
            LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
            http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);
            log.info("LoginFilter added to SecurityFilterChain");

            // 세션 설정: STATELESS
            http.sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            log.debug("Session management set to STATELESS");

            // CORS 설정
            http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://meetus-meetup.netlify.app"));
                    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Arrays.asList("*", "Set-Cookie"));
                    configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));
                    configuration.setMaxAge(3600L);
                    log.debug("CORS configuration set: {}", configuration);
                    return configuration;
                }
            }));
            log.info("CORS configuration applied successfully");

            log.info("SecurityFilterChain configured successfully");
            return http.build();
        } catch (Exception e) {
            log.error("Error configuring SecurityFilterChain", e);
            throw new CustomException(ErrorCode.MEMBER_UNAUTHORIZED);
        }
    }
}
