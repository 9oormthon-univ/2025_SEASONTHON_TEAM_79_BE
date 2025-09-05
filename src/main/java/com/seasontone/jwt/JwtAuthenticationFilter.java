package com.seasontone.jwt;


import com.seasontone.Entity.User;
import com.seasontone.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
/*
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    String token = jwtUtil.resolveToken(request);
    log.debug("[JwtAuthFilter] path={}, hasToken={}", request.getRequestURI(), token != null);

    if (!StringUtils.hasText(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      if (jwtUtil.validateToken(token)) {
        Long uid = jwtUtil.getUserId(token);
        log.debug("[JwtAuthFilter] token valid. userId={}", uid);
        User user = userRepository.findById(uid)
            .orElseThrow(() -> new NullPointerException("user not found: " + uid));
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, "", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
        log.debug("[JwtAuthFilter] validateToken=false");
      }
    } catch (Exception e) {
      log.warn("[JwtAuthFilter] token invalid: {}", e.toString());
    }

    filterChain.doFilter(request, response);
  }


}
 */
