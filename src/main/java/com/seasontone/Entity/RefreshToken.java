package com.seasontone.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/*
@Setter
@Getter
@RedisHash(value = "userId")
public class RefreshToken {
	@Id
	private Long id;
	@Indexed
	private String refreshToken;
	@TimeToLive
	private Long expirationTime;

	public RefreshToken(Long id, String refreshToken, Long expirationTime) {
		this.id = id;
		this.refreshToken = refreshToken;
		this.expirationTime = expirationTime;
	}
}

 */
