package com.seasontone.dto.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
  private String resetToken;
  private String newPassword;
}
