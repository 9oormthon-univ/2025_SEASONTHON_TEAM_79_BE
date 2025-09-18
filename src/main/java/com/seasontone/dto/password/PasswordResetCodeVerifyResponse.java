package com.seasontone.dto.password;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCodeVerifyResponse {
  private boolean verified;
  private String resetToken; // verified=true일 때 토큰 반환
}