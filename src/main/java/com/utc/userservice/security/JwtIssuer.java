package com.utc.userservice.security;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtIssuer {

  @Value("${jwt.secret-base64}")
  private String secretBase64;

  @Value("${jwt.issuer}")
  private String issuer;

  @Value("${jwt.access-ttl-seconds}")
  private long accessTtl;

  private SecretKey key;

  @PostConstruct
  void init() {
    byte[] bytes = Base64.getDecoder().decode(secretBase64);
    this.key = new SecretKeySpec(bytes, "HmacSHA256");
  }

  public String issue(String subjectEmail, List<String> roles) {
    Instant now = Instant.now();
    return Jwts.builder()
      .subject(subjectEmail)
      .issuer(issuer)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(accessTtl)))
      .claim("roles", roles)
      .signWith(key, Jwts.SIG.HS256)
      .compact();
  }
}
