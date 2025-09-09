package com.utc.userservice.security;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http, JwtIssuer jwtIssuer) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/actuator/**", "/auth/public/**").permitAll()
        // /auth/** 에 접근하면 로그인 흐름으로 진입(미인증 시 구글로 리다이렉트)
        .requestMatchers("/auth/**").authenticated()
        .anyRequest().permitAll()
      )
      .oauth2Login(oauth -> oauth.successHandler(successHandler(jwtIssuer)))
      .logout(lo -> lo.logoutUrl("/auth/logout").logoutSuccessUrl("/"));

    return http.build();
  }

  private AuthenticationSuccessHandler successHandler(JwtIssuer jwtIssuer) {
    return (request, response, authentication) -> {
      OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

      // 구글 사용자 정보에서 이메일 추출
      String email = (String) token.getPrincipal().getAttributes().get("email");

      // TODO: 필요 시 DB upsert

      // 액세스 JWT 발급 (역할 예시로 ROLE_USER)
      String accessJwt = jwtIssuer.issue(email, List.of("ROLE_USER"));

      // 쿠키 저장 (로컬 개발: Secure 생략)
      Cookie cookie = new Cookie("ACCESS_TOKEN", accessJwt);
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      // 운영(HTTPS)이라면 반드시 Secure + SameSite=None 필요
      // cookie.setSecure(true);
      // cookie.setAttribute("SameSite", "None");

      response.addCookie(cookie);

      // 프론트/게이트웨이로 안내용 리다이렉트
      response.sendRedirect("/auth/login-success");
    };
  }
}
