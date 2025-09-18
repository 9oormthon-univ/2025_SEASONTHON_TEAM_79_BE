package com.seasontone.domain.users;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@RedisHash("passwordResetCode")
public class PasswordResetCode {
  @Id
  private String email;
  private String code;
  @TimeToLive
  private Long expirationTime; // seconds

  public PasswordResetCode(String email, String code, Long expirationTime) {
    this.email = email;
    this.code = code;
    this.expirationTime = expirationTime;
  }
}
