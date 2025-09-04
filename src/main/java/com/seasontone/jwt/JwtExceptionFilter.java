package com.seasontone.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			String message = e.getMessage();
			if ("시그니처 검증에 실패한 Token 입니다.".equals(message)) {
				setResponse(response, "시그니처 검증에 실패한 Token 입니다.");
			} else if ("손상된 Token 입니다.".equals(message)) {
				setResponse(response, "손상된 Token 입니다.");
			} else if ("유효하지 않은 Access Token입니다.".equals(message)) {
				setResponse(response, "유효하지 않은 Access Token입니다.");
			} else if ("지원하지 않는 Token 입니다.".equals(message)) {
				setResponse(response, "지원하지 않는 Token 입니다.");
			} else if ("존재하지 않는 회원입니다.".equals(message)) {
				setResponse(response, "존재하지 않는 회원입니다.");
			}
		}
	}

	private void setResponse(HttpServletResponse response, String message) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> jsonResponse = new HashMap<>();
		jsonResponse.put("message", message);
		objectMapper.writeValue(response.getWriter(), jsonResponse);
	}
}
