package com.seasontone.config;

import com.seasontone.jwt.JwtAuthenticationFilter;
import com.seasontone.jwt.JwtExceptionFilter;
import com.seasontone.jwt.test.XDevUserIdAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

	private final JwtExceptionFilter jwtExceptionFilter;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
  /*
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.httpBasic(httpBasic -> httpBasic.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(
						sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.formLogin(formLogin -> formLogin.disable())
				.logout(logout -> logout.disable())
				.headers(headers -> headers
						.frameOptions(frameOptions -> frameOptions.disable())
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/sc", "/env").permitAll()
						.requestMatchers(
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html"
						).permitAll()
						.requestMatchers("/user/**").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
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
