package com.utc.userservice.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/auth/login-success")
  public String loginSuccess(@AuthenticationPrincipal OAuth2AuthenticationToken auth) {
    String email = (auth != null) ? (String) auth.getPrincipal().getAttributes().get("email") : "unknown";
    return "Login Success: " + email;
  }

  // 게이트웨이 통해 접근할 테스트 엔드포인트 (게이트웨이에서 JWT로 보호)
  @GetMapping("/user/hello")
  public String userHello() {
    return "Hello from userservice";
  }
}
