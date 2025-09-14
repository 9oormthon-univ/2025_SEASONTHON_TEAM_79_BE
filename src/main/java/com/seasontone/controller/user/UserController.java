package com.seasontone.controller.user;

import com.seasontone.dto.user.EmailCodeRequest;
import com.seasontone.dto.user.EmailCodeResponse;
import com.seasontone.dto.user.EmailCodeVerifyRequest;
import com.seasontone.dto.user.EmailCodeVerifyResponse;
import com.seasontone.dto.user.LoginRequest;
import com.seasontone.dto.user.LoginResponse;
import com.seasontone.dto.user.LoginResult;
import com.seasontone.dto.user.ProfilesResponse;
import com.seasontone.dto.user.RegisterRequest;
import com.seasontone.dto.user.RegisterResponse;
import com.seasontone.dto.user.UpdateProfilesRequest;
import com.seasontone.dto.user.UpdateProfilesResponse;
import com.seasontone.domain.users.User;
import com.seasontone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(userService.register(request));
	}


	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		LoginResult loginResult = userService.login(request);

		String accessToken = loginResult.getAccessToken();
		String refreshToken = loginResult.getRefreshToken();
		LoginResponse loginResponse = loginResult.getLoginResponse();

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", "Bearer " + accessToken);
		httpHeaders.set("Authorization_refresh", "Bearer " + refreshToken);

		return new ResponseEntity<> (loginResponse, httpHeaders, HttpStatus.OK);
	}

	@GetMapping("/email/check")
	public ResponseEntity<Void> checkEmail (@RequestParam String email) {
		userService.checkEmail(email);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/email/code/request")
	public ResponseEntity<EmailCodeResponse> requestEmailCode(@RequestBody EmailCodeRequest request) {
		return ResponseEntity.ok(userService.requestEmailCode(request));
	}

	@PostMapping("/email/code/verify")
	public ResponseEntity<EmailCodeVerifyResponse> verifyEmailCode(@RequestBody EmailCodeVerifyRequest request) {
		return ResponseEntity.ok(userService.verifyEmailCode(request));
	}

	@GetMapping("/profiles")
	public ResponseEntity<ProfilesResponse> getProfiles(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.getProfiles(user));
	}

	@PatchMapping("/profiles")
	public ResponseEntity<UpdateProfilesResponse> updateProfiles(@RequestBody UpdateProfilesRequest request, @AuthenticationPrincipal User user) {
		return ResponseEntity.ok(userService.updateProfiles(request, user));
	}

	@DeleteMapping("/profiles")
	public ResponseEntity<String> deleteProfiles(@AuthenticationPrincipal User user) {
		userService.deleteProfiles(user);
		return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴 완료");
	}

}

