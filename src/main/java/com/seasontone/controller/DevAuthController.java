package com.seasontone.controller;

import com.seasontone.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Profile("local") // 로컬에서만 열고 싶으면 유지, 아니면 제거
public class DevAuthController {

  private final JwtUtil jwtUtil;

  public record DevTokenRequest(Long userId, String name) {}

  @PostMapping("/dev-token")
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> issue(@RequestBody DevTokenRequest req) {
    String access = jwtUtil.generateAccessToken(req.userId(), req.name());
    return Map.of(
        "accessToken", access,
        "tokenType", "Bearer",
        "issuedAt", Instant.now().toString()
    );
  }
}