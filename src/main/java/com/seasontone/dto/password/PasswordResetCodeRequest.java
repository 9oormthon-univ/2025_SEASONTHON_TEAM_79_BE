package com.seasontone.dto.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetCodeRequest {
  private String email;
}
