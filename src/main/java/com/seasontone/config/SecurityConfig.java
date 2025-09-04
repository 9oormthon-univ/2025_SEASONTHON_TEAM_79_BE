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

  private final XUserIdAuthFilter xUserIdAuthFilter;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/sc", "/env").permitAll()
            .anyRequest().permitAll()   // 개발 중 전체 오픈
        )
        .httpBasic(Customizer.withDefaults());

    //유저 필터 (삭제, 수정 유저만 할 수 있게)
    //user가 없기 때문에 임시방편으로 해둠.
    http.addFilterBefore(xUserIdAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}