package com.seasontone.config;

import com.seasontone.jwt.JwtAuthenticationFilter;
import com.seasontone.jwt.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
						//Spring Security에서 프리플라이트 전면허용 옵션
						.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
								.requestMatchers("/sc", "/env").permitAll()
								.requestMatchers(
										"/v3/api-docs/**",
										"/swagger-ui/**",
										"/swagger-ui.html"
								).permitAll()
								//.requestMatchers(HttpMethod.GET,"/api/**").permitAll()
								.requestMatchers(HttpMethod.GET,"/map/**").permitAll()
								.requestMatchers("/users/**").permitAll()

						.requestMatchers(HttpMethod.GET,
								"/api/checklists",
								"/api/checklists/*",
								"/api/checklists/search"
						).permitAll()

						//로그인 필요한 GET
						.requestMatchers(HttpMethod.GET,
								"/api/checklists/mine",
								"/api/checklists/region"
						).authenticated()

						//체크리스트 생성/수정/삭제/업로드는 인증 필요
						.requestMatchers(HttpMethod.POST,   "/api/checklists/**").authenticated()
						.requestMatchers(HttpMethod.PUT,    "/api/checklists/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/checklists/**").authenticated()
						.anyRequest().authenticated()
				)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
		return http.build();
	}

}

