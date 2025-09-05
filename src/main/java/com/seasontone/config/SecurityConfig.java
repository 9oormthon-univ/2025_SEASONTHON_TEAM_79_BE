package com.seasontone.config;

import com.seasontone.security.XUserIdAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  //보안 걸어서..

  private final XUserIdAuthFilter xUserIdAuthFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/s3/ping", "/sc", "/env").permitAll()
            .anyRequest().permitAll() // 개발 단계: 전체 오픈
        )
        .httpBasic(Customizer.withDefaults());

    // 등록 위치는 UsernamePasswordAuthenticationFilter 이전
    http.addFilterBefore(xUserIdAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


/*
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

 */

}