package com.seasontone.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailCodeVerifyRequest {
    private String email;
    private String code;
}
