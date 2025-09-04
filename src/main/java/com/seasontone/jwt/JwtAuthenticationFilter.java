package com.seasontone.jwt;

import com.seasontone.entity.user.User;
import com.seasontone.repository.user.UserRepository;
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

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String findAccessToken = jwtUtil.resolveToken(request);

		if (!StringUtils.hasText(findAccessToken)) {
			doFilter(request, response, filterChain);
			return;
		}

		if (findAccessToken != null && jwtUtil.validateToken(findAccessToken)) {
			User findUser = userRepository.findById(jwtUtil.getUserId(findAccessToken)).orElseThrow(NullPointerException::new);

			Authentication auth = getAuthentication(findUser);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
	}

	public Authentication getAuthentication(User user) {
		return new UsernamePasswordAuthenticationToken(user, "",
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
