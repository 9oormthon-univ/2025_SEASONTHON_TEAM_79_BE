package com.seasontone.jwt.test;

import com.seasontone.Entity.User;
import com.seasontone.repository.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
public class XDevUserIdAuthFilter extends OncePerRequestFilter {

  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String raw = request.getHeader("X-USER-ID");
      if (raw != null && !raw.isBlank()) {
        Long uid = parseLongOrNull(raw);
        if (uid != null) {
          Optional<User> opt = userRepository.findById(uid);
          if (opt.isPresent()) {
            User u = opt.get();
            AuthUser me = new AuthUser(u.getId(), u.getUsername(), u.getEmail()); // 필요 필드에 맞게
            var auth = new UsernamePasswordAuthenticationToken(
                me, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);
          } else {
            // 유저가 없으면 인증 세팅하지 않고 그냥 통과(또는 보호 엔드포인트에서 401/403)
            SecurityContextHolder.clearContext();
          }
        } else {
          SecurityContextHolder.clearContext();
        }
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      // 어떤 예외도 500 터뜨리지 않게 방어
      SecurityContextHolder.clearContext();
      filterChain.doFilter(request, response);
    }
  }

  private Long parseLongOrNull(String s) {
    try { return Long.valueOf(s); } catch (Exception e) { return null; }
  }


}
