package com.seasontone.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@RedisHash(value = "emailCode")
public class EmailCode {
    @Id
    private String email;
    private String code;
    @TimeToLive
    private Long expirationTime;

    public EmailCode(String email, String code, Long expirationTime) {
        this.email = email;
        this.code = code;
        this.expirationTime = expirationTime;
    }
}
