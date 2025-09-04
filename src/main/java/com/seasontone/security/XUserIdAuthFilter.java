package com.seasontone.security;

import com.seasontone.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class XUserIdAuthFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String raw = req.getHeader("X-USER-ID");
    if (raw != null && !raw.isBlank()) {
      try {
        Long userId = Long.parseLong(raw.trim());
        userRepository.findById(userId).ifPresent(u -> {
          // 이름/이메일 쓸거면 유저 엔티티 필드에 맞춰 getName()/getEmail() 등으로 교체
          //굳이 써야하나 싶기도 하고..
          AuthUser principal = new AuthUser(u.getId(), u.getName(), null);
          var auth = new UsernamePasswordAuthenticationToken(principal, "N/A", List.of());
          SecurityContextHolder.getContext().setAuthentication(auth);
        });
      } catch (NumberFormatException ignore) {}
    }
    chain.doFilter(req, res);
  }
}