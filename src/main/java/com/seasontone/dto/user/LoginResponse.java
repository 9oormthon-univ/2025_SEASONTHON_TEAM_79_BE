package com.seasontone.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class LoginResponse {
    private Long userId;
}
