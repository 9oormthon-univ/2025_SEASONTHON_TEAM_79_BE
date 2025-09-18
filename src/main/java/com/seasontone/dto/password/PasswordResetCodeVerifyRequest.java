package com.seasontone.dto.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetCodeVerifyRequest {
  private String email;
  private String code;
}
