package com.seasontone.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();

    // 프론트(로컬 Vite)와 배포 도메인 허용
    c.setAllowedOrigins(List.of(
        "http://localhost:5173",
        "http://43.200.96.110",
        "http://43.200.96.110:8080"
    ));
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    //CORS Bean을 관대한 설정으로
    c.setAllowedHeaders(List.of("*"));
    c.setAllowedHeaders(List.of("Authorization","Content-Type"));
    c.setAllowCredentials(true);     // 쿠키/인증 헤더 허용시 true
    c.setMaxAge(3600L);              // preflight 캐시(초)

    UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**", c);
    return s;
  }
}
