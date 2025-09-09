package com.utc.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootLog {
  @Bean
  CommandLineRunner logOauth(
      @Value("${spring.security.oauth2.client.registration.google.client-id:}") String cid,
      @Value("${spring.security.oauth2.client.registration.google.client-secret:}") String csec,
      @Value("${jwt.secret-base64:}") String jwt
  ) {
    return args -> {
      System.out.println("[userservice] clientId(len)=" + (cid==null?0:cid.length()));
      if (cid != null && cid.length() >= 10) {
        System.out.println("[userservice] clientId(head)=" + cid.substring(0, 10) + "...");
      }
      System.out.println("[userservice] clientSecret(len)=" + (csec==null?0:csec.length()));
      System.out.println("[userservice] JWT_SECRET_BASE64(len)=" + (jwt==null?0:jwt.length()));
    };
  }
}
