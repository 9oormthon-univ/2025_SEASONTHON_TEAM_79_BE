package com.seasontone.config;

import com.seasontone.jwt.JwtAuthenticationFilter;
import com.seasontone.jwt.JwtExceptionFilter;
import com.seasontone.security.XDevUserIdAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  /*
  private final JwtExceptionFilter jwtExceptionFilter;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(form -> form.disable())
        .logout(lo -> lo.disable())
        .headers(h -> h.frameOptions(f -> f.disable()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/sc", "/env", "/error").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/user/**").permitAll()
            .requestMatchers("/api/auth/dev-token").permitAll() // 임시 토큰 발급 열어둠
            .requestMatchers(HttpMethod.GET, "/api/checklists/**").permitAll()
            .requestMatchers(HttpMethod.POST,   "/api/checklists/**").permitAll() //POST 인증 일단 없앰
            .requestMatchers(HttpMethod.PUT,    "/api/checklists/**").authenticated()
            .requestMatchers(HttpMethod.PATCH,  "/api/checklists/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/checklists/**").authenticated()
            .anyRequest().permitAll()
        );

    //예외 필터
    http.addFilterBefore(jwtExceptionFilter, org.springframework.security.web.context.SecurityContextHolderFilter.class);

    //JWT 인증 필터
    http.addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
  
   */
  //임시필터
  private final XDevUserIdAuthFilter xDevUserIdAuthFilter;

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
    http.addFilterBefore(xDevUserIdAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}