package com.seasontone.domain.users;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

// 코드 인증 후 발급되는 일회용 토큰: 토큰↔이메일, 10분 TTL
@Getter
@Setter
@RedisHash("passwordResetToken")
public class PasswordResetToken {
  @Id
  private String token;
  private String email;
  @TimeToLive
  private Long expirationTime; // seconds

  public PasswordResetToken(String token, String email, Long expirationTime) {
    this.token = token;
    this.email = email;
    this.expirationTime = expirationTime;
  }
}