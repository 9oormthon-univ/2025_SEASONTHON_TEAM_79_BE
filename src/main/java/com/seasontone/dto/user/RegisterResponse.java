package com.seasontone.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class RegisterResponse {
    private Long user_id;
    private Boolean is_email_verified;
}
